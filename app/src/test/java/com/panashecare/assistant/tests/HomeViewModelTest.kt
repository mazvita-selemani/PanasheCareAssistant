package com.panashecare.assistant.tests

import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.ShiftResult
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.HomeScreenViewModel
import io.mockk.coVerify
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var shiftRepository: ShiftRepository
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: HomeScreenViewModel

    private val testUserId = "testUserId"
    private val testUser = User(firstName = "John", lastName = "Doe")
    private val pastShift = Shift(id = "past1")
    private val futureShift = Shift(id = "future1")

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        shiftRepository = mockk()
        userRepository = mockk()

        // Mock userRepository.getUserById callback with testUser
        coEvery {
            userRepository.getUserById(testUserId, any())
        } answers {
            val callback = secondArg<(User?) -> Unit>()
            callback(testUser)
        }

        // Mock shiftRepository flows to emit success for past and future shifts
        coEvery { shiftRepository.getLatestPastShift(user = testUser) } returns flowOf(
            ShiftResult.Success(pastShift)
        )
        coEvery { shiftRepository.getLatestFutureShift(user = testUser) } returns flowOf(
            ShiftResult.Success(futureShift)
        )

        viewModel = HomeScreenViewModel(shiftRepository, userRepository, testUserId)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_that_user_is_loaded_when_viewmodel_initializes() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testUser, viewModel.user.value)
        assertEquals(testUser, viewModel.state.user)

        coVerify { userRepository.getUserById(testUserId, any()) }
    }

    @Test
    fun test_that_past_shift_is_loaded_when_user_is_set() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.latestShiftState.value
        assertTrue(result is ShiftResult.Success)
        assertEquals(pastShift, (result as ShiftResult.Success).shift)
        assertEquals(pastShift, viewModel.state.pastShift)

        coVerify { shiftRepository.getLatestPastShift(user = testUser) }
    }

    @Test
    fun test_that_future_shift_is_loaded_when_user_is_set() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.latestFutureState.value
        assertTrue(result is ShiftResult.Success)
        assertEquals(futureShift, (result as ShiftResult.Success).shift)
        assertEquals(futureShift, viewModel.state.futureShift)

        coVerify { shiftRepository.getLatestFutureShift(user = testUser) }
    }

    @Test
    fun test_that_shift_states_are_reset_when_user_is_null() = runTest {
        // Mock userRepository.getUserById to return null user
        coEvery {
            userRepository.getUserById(testUserId, any())
        } answers {
            val callback = secondArg<(User?) -> Unit>()
            callback(null)
        }

        // Create new ViewModel instance with null user
        viewModel = HomeScreenViewModel(shiftRepository, userRepository, testUserId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, viewModel.user.value)
        assertEquals(null, viewModel.state.user)
        assertEquals(null, viewModel.state.pastShift)
        assertEquals(null, viewModel.state.futureShift)
        assertEquals(ShiftResult.Loading, viewModel.latestShiftState.value)
        assertEquals(ShiftResult.Loading, viewModel.latestFutureState.value)

        coVerify { userRepository.getUserById(testUserId, any()) }
    }
}
