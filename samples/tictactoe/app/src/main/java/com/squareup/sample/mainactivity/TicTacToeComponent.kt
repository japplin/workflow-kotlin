package com.squareup.sample.mainactivity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import com.squareup.sample.authworkflow.AuthComposeWorkflow
import com.squareup.sample.authworkflow.AuthService
import com.squareup.sample.authworkflow.AuthService.AuthRequest
import com.squareup.sample.authworkflow.AuthService.AuthResponse
import com.squareup.sample.authworkflow.AuthService.SecondFactorRequest
import com.squareup.sample.authworkflow.RealAuthComposeWorkflow
import com.squareup.sample.authworkflow.RealAuthService
import com.squareup.sample.gameworkflow.RealGameLog
import com.squareup.sample.gameworkflow.RealRunGameComposeWorkflow
import com.squareup.sample.gameworkflow.RealTakeTurnsComposeWorkflow
import com.squareup.sample.gameworkflow.RunGameComposeWorkflow
import com.squareup.sample.gameworkflow.TakeTurnsComposeWorkflow
import com.squareup.sample.mainworkflow.TicTacToeComposeWorkflow
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import timber.log.Timber

/**
 * Pretend generated code of a pretend DI framework.
 */
class TicTacToeComponent : ViewModel() {
  private val countingIdlingResource = CountingIdlingResource("AuthServiceIdling")
  val idlingResource: IdlingResource = countingIdlingResource

  private val realAuthService = RealAuthService()

  private val authService = object : AuthService {
    override fun login(request: AuthRequest): Single<AuthResponse> {
      return realAuthService.login(request)
        .doOnSubscribe { countingIdlingResource.increment() }
        .doAfterTerminate { countingIdlingResource.decrement() }
    }

    override fun secondFactor(request: SecondFactorRequest): Single<AuthResponse> {
      return realAuthService.secondFactor(request)
        .doOnSubscribe { countingIdlingResource.increment() }
        .doAfterTerminate { countingIdlingResource.decrement() }
    }
  }

  private fun authWorkflow(): AuthComposeWorkflow = RealAuthComposeWorkflow(authService)

  private fun gameLog() = RealGameLog(mainThread())

  private fun gameWorkflow(): RunGameComposeWorkflow = RealRunGameComposeWorkflow(takeTurnsWorkflow(), gameLog())

  private fun takeTurnsWorkflow(): TakeTurnsComposeWorkflow = RealTakeTurnsComposeWorkflow()

  private val ticTacToeWorkflow = TicTacToeComposeWorkflow(authWorkflow(), gameWorkflow())

  fun ticTacToeModelFactory(owner: AppCompatActivity): TicTacToeModel.Factory =
    TicTacToeModel.Factory(owner, ticTacToeWorkflow, traceFilesDir = owner.filesDir)

  companion object {
    init {
      Timber.plant(Timber.DebugTree())

      val stock = Thread.getDefaultUncaughtExceptionHandler()
      Thread.setDefaultUncaughtExceptionHandler { thread, error ->
        Timber.e(error)
        stock?.uncaughtException(thread, error)
      }
    }
  }
}
