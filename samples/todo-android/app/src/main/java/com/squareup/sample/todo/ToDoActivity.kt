package com.squareup.sample.todo

import android.os.Bundle
import android.view.Choreographer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.molecule.AndroidUiFrameClock
import app.cash.molecule.launchMolecule
import com.squareup.sample.container.overviewdetail.OverviewDetailContainer
import com.squareup.sample.todo.unmanagedstate.TodoListsAppComposeWorkflow
import com.squareup.workflow1.diagnostic.tracing.TracingWorkflowInterceptor
import com.squareup.workflow1.ui.ViewRegistry
import com.squareup.workflow1.ui.WorkflowLayout
import com.squareup.workflow1.ui.WorkflowUiExperimentalApi
import com.squareup.workflow1.ui.backstack.BackStackContainer
import com.squareup.workflow1.ui.renderWorkflowIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.plus
import java.io.File

@OptIn(WorkflowUiExperimentalApi::class)
class ToDoActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val model: ToDoModel by viewModels()

    setContentView(
      WorkflowLayout(this).apply {
        start(model.ensureWorkflow(traceFilesDir = filesDir), viewRegistry)
      }
    )
  }

  private companion object {
    val viewRegistry = ViewRegistry(OverviewDetailContainer, BackStackContainer)
  }
}

class ToDoModel(private val savedState: SavedStateHandle) : ViewModel() {
  private var renderings: StateFlow<Any>? = null

  @OptIn(WorkflowUiExperimentalApi::class)
  fun ensureWorkflow(traceFilesDir: File): StateFlow<Any> {
    if (renderings == null) {
      val traceFile = traceFilesDir.resolve("workflow-trace-todo.json")
      val useMolecule = true
      renderings = if (useMolecule) {
        (viewModelScope + AndroidUiFrameClock(Choreographer.getInstance()))
          .launchMolecule { TodoListsAppComposeWorkflow.render(Unit) {} }
      } else {
        renderWorkflowIn(
          workflow = TodoListsAppWorkflow,
          scope = viewModelScope,
          savedStateHandle = savedState,
          interceptors = listOf(TracingWorkflowInterceptor(traceFile))
        )
      }
    }

    return renderings!!
  }
}
