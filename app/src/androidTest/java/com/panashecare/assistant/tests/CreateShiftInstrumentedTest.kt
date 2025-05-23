package com.panashecare.assistant.tests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.model.objects.Gender
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.ShiftStatus
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.viewModel.shiftManagement.CreateShiftViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CreateShiftViewModelInstrumentedTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var shiftRepository: ShiftRepository
    private lateinit var viewModel: CreateShiftViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        userRepository = mockk()
        shiftRepository = mockk()

        every { userRepository.getCarers() } returns flowOf(
            listOf(
                dummyUser1, dummyUser2, dummyUser3
            )
        )

        every { shiftRepository.createShift(any(), any()) } answers {
            val callback = secondArg<(Boolean) -> Unit>()
            callback(true)
        }

        viewModel = CreateShiftViewModel(userRepository, shiftRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_that_carers_are_loaded_when_viewmodel_initializes() = runTest {
        advanceUntilIdle()
        Assert.assertEquals(2, viewModel.state.carers?.size)
        Assert.assertEquals("John", viewModel.state.carers?.first()?.firstName)
    }

    @Test
    fun test_that_createShift_does_not_call_repo_when_validation_fails() = runTest {
        viewModel.createShift(Shift())
        verify(exactly = 0) { shiftRepository.createShift(any(), any()) }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun test_that_createShift_calls_repo_when_validation_passes() = runTest {
        val now = System.currentTimeMillis()
        viewModel.state = viewModel.state.copy(
            startDate = now,
            endDate = now + 3600000,
            startTime = mockk(relaxed = true),
            endTime = mockk(relaxed = true),
            selectedCarer = dummyUser1
        )

        val shift = Shift(
            adminName = dummyUser3,
            healthAideName = viewModel.state.selectedCarer,
            shiftDate = now.toString(),
            shiftDuration = "1h",
            shiftEndTime = "12:00",
            shiftEndDate = now.toString(),
            shiftStatus = ShiftStatus.REQUESTED,
            shiftTime = "11:00"
        )

        viewModel.createShift(shift)
        advanceUntilIdle()
        verify(exactly = 1) { shiftRepository.createShift(shift, any()) }
    }
}

val dummyUser1 = User(
    id = "user1",
    gender = Gender.MALE,
    profileImageRef = 101,
    firstName = "John",
    lastName = "Doe",
    phoneNumber = "+441234567890",
    email = "john.doe@example.com",
    userType = UserType.CARER,
)

val dummyUser2 = User(
    id = "user2",
    gender = Gender.FEMALE,
    profileImageRef = 102,
    firstName = "Emma",
    lastName = "Smith",
    phoneNumber = "+441987654321",
    email = "emma.smith@example.com",
    userType = UserType.ADMIN,
    patientFirstName = "Sophia",
    patientLastName = "Brown"
)

val dummyUser3 = User(
    id = "user3",
    gender = Gender.MALE,
    profileImageRef = 103,
    firstName = "Michael",
    lastName = "Brown",
    phoneNumber = "+447700900123",
    email = "michael.brown@example.com",
    userType = UserType.CARER,
)



