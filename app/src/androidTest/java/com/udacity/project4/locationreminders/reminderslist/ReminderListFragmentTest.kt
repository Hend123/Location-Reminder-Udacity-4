package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.test.inject
import com.udacity.project4.R
import org.hamcrest.CoreMatchers.not
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
//UI Test
class ReminderListFragmentTest : KoinTest {
    private val repository: ReminderDataSource by inject()
    private lateinit var appContext: Application
    private val reminder1 = ReminderDTO(
        "Reminder1", "Description1",
        "Location1", 1.0, 1.0, "1"
    )
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    val activityRule = ActivityTestRule(RemindersActivity::class.java)

    @Before
    fun init() {
        //stop koin
        stopKoin()
        appContext = ApplicationProvider.getApplicationContext()
        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            //Declare a ViewModel - be later inject into Fragment with dedicated injector using by viewModel()
            viewModel {
                RemindersListViewModel(
                    get(),
                    get() as ReminderDataSource
                )
            }
            //Declare singleton definitions to be later injected using by inject()
            single { FakeDataSource() as ReminderDataSource }
        }

        startKoin {
            androidContext(appContext)
            modules(listOf(myModule))
        }
    }
    @After
    fun cleanupDb() = runBlockingTest {
        repository.deleteAllReminders()
    }

    @Test
    fun reminderShownInRV() = runBlockingTest {
        //GIVEN - reminder1
          repository.saveReminder(reminder1)
        //WHEN - ReminderListFragment is displayed
        launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)

        //THEN - reminders is displayed
        onView(withText(reminder1.title)).check(matches(isDisplayed()))
        onView(withText(reminder1.description)).check(matches(isDisplayed()))
        onView(withText(reminder1.location)).check(matches(isDisplayed()))
        onView((withId(R.id.noDataTextView))).check(matches(not(isDisplayed())))
    }

    @Test
    fun clickTask_navigateToSaveReminderFragment() = runBlockingTest{
        //GIVEN - ReminderListFragment is displayed
        val scenario =  launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        //WHEN - Click on add reminder
        onView((withId(R.id.addReminderFAB))).perform(click())
        //THEN - navigate to SaveReminderFragment
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())

    }

}