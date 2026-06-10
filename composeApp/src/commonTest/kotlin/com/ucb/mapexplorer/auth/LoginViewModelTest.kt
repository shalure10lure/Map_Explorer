package com.ucb.mapexplorer.auth

import app.cash.turbine.test
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import com.ucb.mapexplorer.auth.domain.usecase.LoginUseCase
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEffect
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEvent
import com.ucb.mapexplorer.auth.presentation.login.viewmodel.LoginViewModel
import io.mockative.any
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // Mockative genera el mock de AuthRepository
    val authRepository = mock(classOf<AuthRepository>())

    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        // Reemplaza el dispatcher de coroutines por uno controlable
        Dispatchers.setMain(testDispatcher)
        val loginUseCase = LoginUseCase(authRepository)
        viewModel = LoginViewModel(loginUseCase)
    }

    @AfterTest
    fun teardown() {
        // Restablece el dispatcher principal
        Dispatchers.resetMain()
    }

    // ── TEST 1: El estado inicial es correcto ─────────────────────────────
    @Test
    fun `estado inicial tiene email y password vacíos`() {
        val state = viewModel.state.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
    }

    // ── TEST 2: Cambiar el email actualiza el estado ───────────────────────
    @Test
    fun `OnEmailChanged actualiza el email en el estado`() {
        viewModel.onEvent(LoginEvent.OnEmailChanged("test@test.com"))
        assertEquals("test@test.com", viewModel.state.value.email)
    }

    // ── TEST 3: Cambiar la contraseña actualiza el estado ─────────────────
    @Test
    fun `OnPasswordChanged actualiza la password en el estado`() {
        viewModel.onEvent(LoginEvent.OnPasswordChanged("123456"))
        assertEquals("123456", viewModel.state.value.password)
    }

    // ── TEST 4: Login con campos vacíos emite ShowError ───────────────────
    @Test
    fun `login con campos vacíos emite ShowError`() = runTest {
        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.OnClick)
            val effect = awaitItem()
            assertTrue(effect is LoginEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── TEST 5: Login fallido emite ShowError ─────────────────────────────
    @Test
    fun `login fallido emite ShowError con mensaje`() = runTest {
        coEvery { authRepository.login("wrong@test.com", "wrongpass") }
            .returns(false)

        viewModel.onEvent(LoginEvent.OnEmailChanged("wrong@test.com"))
        viewModel.onEvent(LoginEvent.OnPasswordChanged("wrongpass"))

        viewModel.effect.test {
            viewModel.onEvent(LoginEvent.OnClick)
            testDispatcher.scheduler.advanceUntilIdle()
            val effect = awaitItem()
            assertTrue(effect is LoginEffect.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }

}
