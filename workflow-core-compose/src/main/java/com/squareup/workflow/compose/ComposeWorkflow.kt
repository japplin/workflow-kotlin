package com.squareup.workflow.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

public interface ComposeWorkflow<in PropsT, out OutputT, out RenderingT> {
  @Composable public fun render(
    renderProps: PropsT,
    output: (OutputT) -> Unit
  ): RenderingT
}

public abstract class StatefulComposeWorkflow<in PropsT, StateT, out OutputT, out RenderingT>
  : ComposeWorkflow<PropsT, OutputT, RenderingT> {

  public abstract fun initialState(props: PropsT): StateT

  @Composable final override fun render(
    renderProps: PropsT,
    output: (OutputT) -> Unit
  ): RenderingT {
    val scope = rememberCoroutineScope()
    val renderContext = remember { RenderContext(scope, initialState(renderProps)) }
    renderContext.updateOnOutput(output)

    return render(renderProps, renderContext.stateFlow.collectAsState().value, renderContext)
  }

  @Composable protected abstract fun render(
    renderProps: PropsT,
    renderState: StateT,
    context: RenderContext
  ): RenderingT

  public inner class RenderContext internal constructor(
    private val scope: CoroutineScope,
    initialState: StateT,
  ) {
    internal val stateFlow: MutableStateFlow<StateT> = MutableStateFlow(initialState)
    private val outputFlow = MutableStateFlow<(OutputT) -> Unit> { }

    public var state: StateT
      get() = stateFlow.value
      set(value) {
        scope.launch {
          stateFlow.emit(value)
        }
      }

    public fun setOutput(output: @UnsafeVariance OutputT) {
      outputFlow.value(output)
    }

    internal fun updateOnOutput(onOutput: (OutputT) -> Unit) {
      scope.launch {
        outputFlow.emit(onOutput)
      }
    }
  }
}

@Composable public fun <OutputT, RenderingT> ComposeWorkflow<Unit, OutputT, RenderingT>.render(
  output: (OutputT) -> Unit
): RenderingT {
  return render(Unit, output)
}
