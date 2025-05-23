package com.panashecare.assistant.tests

import androidx.compose.material3.ExperimentalMaterial3Api
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.shiftManagement.CreateShiftViewModel
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
class CreateShiftViewModelTest {

    private val userRepository = mockk<UserRepository>()
    private val shiftRepository = mockk<ShiftRepository>(relaxed = true)
    private lateinit var viewModel: CreateShiftViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { userRepository.getCarers() } returns flowOf(emptyList())
        viewModel = CreateShiftViewModel(userRepository, shiftRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads carers from repository`() = runTest {
        val carers = listOf(User(id = "1", firstName = "John"), User(id = "2", firstName = "Jane"))
        coEvery { userRepository.getCarers() } returns flowOf(carers)

        viewModel = CreateShiftViewModel(userRepository, shiftRepository)
        advanceUntilIdle()

        assertEquals(carers, viewModel.state.carers)
    }

    @Test
    fun `validateFields returns false and populates errors when fields are missing`() {
        val result = viewModel.validateFields()
        assertFalse(result)
        assertTrue(viewModel.state.errors.containsKey("startTime"))
        assertTrue(viewModel.state.errors.containsKey("endTime"))
        assertTrue(viewModel.state.errors.containsKey("startDate"))
        assertTrue(viewModel.state.errors.containsKey("endDate"))
        assertTrue(viewModel.state.errors.containsKey("selectedCarer"))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun `validateFields returns true when all required fields are set correctly`() {
        viewModel.state = viewModel.state.copy(
            startTime = mockk(),
            endTime = mockk(),
            startDate = 1_000_000L,
            endDate = 2_000_000L,
            selectedCarer = User(id = "1", firstName = "Carer A")
        )

        val result = viewModel.validateFields()
        assertTrue(result)
        assertTrue(viewModel.state.errors.isEmpty())
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun `updateChecked true sets endDate to startDate`() {
        val startDate = 1_000_000L
        viewModel.state = viewModel.state.copy(startDate = startDate)

        viewModel.updateChecked(true)

        assertTrue(viewModel.state.isChecked)
        assertEquals(startDate, viewModel.state.endDate)
    }

    @Test
    fun `updateNotes updates the notes state`() {
        viewModel.updateNotes("This is a test note")
        assertEquals("This is a test note", viewModel.state.notes)
    }

    @Test
    fun `createShift does not call repository when validation fails`() = runTest {
        viewModel.createShift(mockk())
        advanceUntilIdle()

        coVerify(exactly = 0) { shiftRepository.createShift(any(), any()) }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun `createShift calls repository when validation passes`() = runTest {
        viewModel.state = viewModel.state.copy(
            startTime = mockk(),
            endTime = mockk(),
            startDate = 1_000_000L,
            endDate = 2_000_000L,
            selectedCarer = User(id = "1", firstName = "Test")
        )

        val shift = mockk<Shift>()

        viewModel.createShift(shift)
        advanceUntilIdle()

        coVerify(exactly = 1) { shiftRepository.createShift(shift, any()) }
    }
}
