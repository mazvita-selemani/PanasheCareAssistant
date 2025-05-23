package com.panashecare.assistant.tests
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.panashecare.assistant.access.UserType
import com.panashecare.assistant.model.objects.Shift
import com.panashecare.assistant.model.objects.User
import com.panashecare.assistant.model.repository.ShiftRepository
import com.panashecare.assistant.model.repository.ShiftResult
import com.panashecare.assistant.model.repository.UserRepository
import com.panashecare.assistant.view.HomeChild
import com.panashecare.assistant.viewModel.HomeScreenViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var userRepository: UserRepository
    private lateinit var shiftRepository: ShiftRepository
    private lateinit var viewModel: HomeScreenViewModel

    private val dispatcher = StandardTestDispatcher()

    private val dummyUser = User(
        id = "1",
        firstName = "John",
        patientFirstName = "Jane",
        userType = UserType.ADMIN,
        profileImageRef = 0
    )

    private val futureShift = Shift(id = "future1")
    private val pastShift = Shift(id = "past1")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        userRepository = mockk()
        shiftRepository = mockk()

        // Mocking repository behavior
        every { userRepository.getUsersRealtime() } returns flowOf(dummyUser)
        every { shiftRepository.getLatestFutureShift(any()) } returns flowOf(ShiftResult.Success(futureShift))
        every { shiftRepository.getLatestPastShift(any()) } returns flowOf(ShiftResult.Success(pastShift))

        // Instantiate the ViewModel
        viewModel = HomeScreenViewModel(
            shiftRepository = shiftRepository,
            userRepository = userRepository,
            userId = dummyUser.id!!
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun homeScreen_displaysCorrectContent_andRespondsToNavigation() {
        // Navigation mocks
        val navigateToProfile = mockk<() -> Unit>(relaxed = true)
        val navigateToCreateShift = mockk<() -> Unit>(relaxed = true)
        val navigateToShiftList = mockk<() -> Unit>(relaxed = true)
        val navigateToPast = mockk<(Shift) -> Unit>(relaxed = true)
        val navigateToFuture = mockk<(Shift) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            HomeChild(
                user = dummyUser,
                navigateToProfile = navigateToProfile,
                navigateToCreateShift = navigateToCreateShift,
                navigateToShiftList = navigateToShiftList,
                navigateToSingleViewForPastShift = navigateToPast,
                navigateToSingleViewForFutureShift = navigateToFuture,
                state = viewModel.state,
                viewModel = viewModel
            )
        }

        // Assert profile image is shown and click triggers navigateToProfile
        composeTestRule
            .onNodeWithContentDescription("Profile image")
            .assertExists()
            .performClick()
        io.mockk.verify { navigateToProfile.invoke() }

        // Assert shift list card is displayed and navigates on click
        composeTestRule
            .onNodeWithText("View All Upcoming shifts")
            .assertExists()
            .performClick()
        io.mockk.verify { navigateToShiftList.invoke() }

        // Assert create shift card is displayed and navigates on click
        composeTestRule
            .onNodeWithText("Create a new shift")
            .assertExists()
            .performClick()
        io.mockk.verify { navigateToCreateShift.invoke() }

        // Assert next shift title is shown
        composeTestRule
            .onNodeWithText("Next shift")
            .assertExists()

        // Assert "Previously Completed" title is shown
        composeTestRule
            .onNodeWithText("Previously Completed")
            .assertExists()

        // Click future shift card and verify callback
        composeTestRule
            .onNodeWithText("future1", substring = true)
            .performClick()
        io.mockk.verify { navigateToFuture.invoke(futureShift) }

        // Click past shift card and verify callback
        composeTestRule
            .onNodeWithText("past1", substring = true)
            .performClick()
        io.mockk.verify { navigateToPast.invoke(pastShift) }
    }

}

