package com.example.vino.ui.todos

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vino.R
import com.example.vino.VinoApplication
import com.example.vino.databinding.TodoFragmentCollectionObjectBinding
import com.example.vino.model.UserViewModel
import com.example.vino.model.UserViewModelFactory
import com.example.vino.model.VinoApiStatus
import com.example.vino.model.Todo
import com.example.vino.ui.adapter.ARG_TODO_TYPE
import com.example.vino.ui.adapter.TodoListAdapter
import com.google.android.material.card.MaterialCardView
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TodoListSubFragment : Fragment(), TodoListAdapter.OnTodoCheckBoxListener {

    private val vinoUserModel: UserViewModel by activityViewModels {
        UserViewModelFactory((requireActivity().application as VinoApplication).repository)
    }
    private var _binding: TodoFragmentCollectionObjectBinding? = null
    private var completed = false
    private lateinit var sortedByDate: MutableList<Todo>
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

    override fun onCheckboxClick(position: Int) {
        // in local database set complete and make request to set complete
        //val todoRemoved = sortedTodoList.removeAt(position)
        val todoRemoved = sortedByDate.removeAt(position)
        adapter.notifyItemRemoved(position)
        vinoUserModel.updateTodo(todoRemoved)
    }

    private fun setUpRecyclerView() {
        // TODO recycler view due date sticky headers
        binding.todoRecyclerView.visibility = View.INVISIBLE // to show progress circle
        // TODO: Fix delete animation?
        binding.todoRecyclerView.itemAnimator = SlideInRightAnimator().apply {
            removeDuration = 100
        }

        adapter = TodoListAdapter(completed, requireContext(), this)
        binding.todoRecyclerView.adapter = adapter
        binding.todoRecyclerView.setHasFixedSize(true)

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.todoRecyclerView) // for swipe delete

        if (completed)
            observeTodos(vinoUserModel.completeTodos)
        else
            observeTodos(vinoUserModel.inCompleteTodos)
    }

    private fun setUpConnectionImageAndText() {
        // on api status, either way DONE or ERROR, hide loading
        vinoUserModel.apiStatus.observe(viewLifecycleOwner, {
            if (it != VinoApiStatus.LOADING)
                binding.progressCircular.hide()
//            if (it == VinoApiStatus.ERROR) // TODO add no connection image and or text
//                binding.connectionStatusText.visibility = View.VISIBLE
            // if DONE hide text
        })
    }

    private fun observeTodos(todosForObserved: LiveData<List<Todo>>) {
        todosForObserved.observe(viewLifecycleOwner, { newTodoList ->
            if (newTodoList.isEmpty()) {
                binding.noTodoImage.visibility = View.VISIBLE
                binding.noTodoText.visibility = View.VISIBLE
            } else { // TODO change text if complete vs incomplete
                binding.noTodoImage.visibility = View.GONE
                binding.noTodoText.visibility = View.GONE
                setTodos(newTodoList)
            }
        })
    }

    private fun setTodos(todoList: List<Todo>) {
        binding.progressCircular.hide()
        binding.todoRecyclerView.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Default) {
            sortedByDate = todoList
                .sortedBy { todo ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = todo.dueDate
//                    calendar.set(Calendar.DAY_OF_MONTH, todo.dueDate.substringAfter("/").toInt())
//                    calendar.set(Calendar.MONTH, todo.dueDate.substringBefore("/").toInt())
                    return@sortedBy calendar.time
                }.toMutableList()
            activity?.runOnUiThread {
                adapter.submitList(sortedByDate)
            }
        }
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
                //sortedTodoList.removeAt(viewHolder.adapterPosition)
                val deletedTodo = sortedByDate.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                vinoUserModel.deleteTodo(deletedTodo)
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
                val cardView = itemView.findViewById<MaterialCardView>(R.id.todo_card_view)
                val radius = 4f * resources.displayMetrics.density // original for card view corners

                if (dX < 0f) // if user slides to left, change to 0 radius so no round corners on when red delete background visible
                    setCardViewCorners(cardView, 0f)
                else  // reset to original
                    setCardViewCorners(cardView, radius)

                val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                val width = height / 3
                val paint = Paint()
                paint.color = Color.parseColor("#722f37") // wine red? or use regular delete red

                // Create the red delete background
                val background = RectF(
                            itemView.right.toFloat() + dX,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                )
                // delete corners same as card view
                val corners = floatArrayOf(
                    0f, 0f,   // Top left radius in px
                    radius, radius,   // Top right radius in px
                    radius, radius,     // Bottom right radius in px
                    0f, 0f      // Bottom left radius in px
                )

                // create the round red delete rectangle
                val path = Path()
                path.addRoundRect(background, corners, Path.Direction.CW)

                // draw on canvas
                c.drawPath(path, paint)

                // get delete icon
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_24)
                val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)

                // place icon
                val iconDest = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(bitmap, null, iconDest, paint)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun setCardViewCorners(cardView: MaterialCardView, radius: Float) {
                cardView.shapeAppearanceModel = cardView.shapeAppearanceModel
                    .toBuilder()
                    .setTopRightCornerSize(radius)
                    .setBottomRightCornerSize(radius)
                    .build()
            }
        }
}