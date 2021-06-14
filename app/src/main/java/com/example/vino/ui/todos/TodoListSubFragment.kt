package com.example.vino.ui.todos

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.databinding.TodoFragmentCollectionObjectBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.VinoApiStatus
import com.example.vino.network.Todo
import com.example.vino.ui.adapter.ARG_TODO_TYPE
import com.example.vino.ui.adapter.TodoListAdapter
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// Instances of this class are fragments representing a single
// object in our collection.
class TodoListSubFragment : Fragment() {

    private val vinoUserModel: UserViewModel by activityViewModels()
    private var _binding: TodoFragmentCollectionObjectBinding? = null
    private var completed = false
    private lateinit var sortedTodoList: MutableList<Todo>
    private lateinit var adapter: TodoListAdapter
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            completed = it.getBoolean(ARG_TODO_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TodoFragmentCollectionObjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpConnectionImageAndText()
        setUpRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        binding.todoRecyclerView.visibility = View.INVISIBLE // to show progress circle
        // TODO: Fix delete animation
        binding.todoRecyclerView.itemAnimator = OvershootInLeftAnimator()
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.todoRecyclerView) // for swipe delete

        vinoUserModel.todos.observe(viewLifecycleOwner, { todoList ->

            binding.progressCircular.hide() // hide progress once user is grabbed
            binding.todoRecyclerView.visibility = View.VISIBLE

            adapter = TodoListAdapter(completed, requireContext())
            binding.todoRecyclerView.adapter = adapter

            lifecycleScope.launch(Dispatchers.Default) {
                val filteredList = todoList.filter { it.completed == completed }
                sortedTodoList = filteredList.toMutableList()

                activity?.runOnUiThread {
                    adapter.submitList(sortedTodoList)
                }
            }
        })

        binding.todoRecyclerView.setHasFixedSize(true)
    }

    private fun setUpConnectionImageAndText() {
        // on api status, either way DONE or ERROR, hide loading
        vinoUserModel.status.observe(viewLifecycleOwner, {
            if (it != VinoApiStatus.LOADING)
                binding.progressCircular.hide()
//            if (it == VinoApiStatus.ERROR)
//                binding.connectionStatusText.visibility = View.VISIBLE
            // if DONE hide text
        })
    }

    // TODO add vibrations on delete
    // swipe left to delete
    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                sortedTodoList.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                val width = height / 3
                val paint = Paint()

                paint.color = Color.parseColor("#D32F2F")
                val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                )
                val radius = 4F * resources.displayMetrics.density
                val corners = floatArrayOf(
                    0F, 0F,   // Top left radius in px
                    radius, radius,   // Top right radius in px
                    radius, radius,     // Bottom right radius in px
                    0f, 0f      // Bottom left radius in px
                )
                val path = Path()
                path.addRoundRect(background, corners, Path.Direction.CW)
                //c.drawRoundRect(background, 10F, 10F, paint)
                c.drawPath(path, paint)
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)
                val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                val iconDest = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top
                        .toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(bitmap, null, iconDest, paint)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }
}