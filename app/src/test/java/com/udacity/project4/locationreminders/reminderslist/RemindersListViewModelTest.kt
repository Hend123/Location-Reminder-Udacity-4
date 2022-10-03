package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersListViewModelTest{
    // Subject under test
    private lateinit var reminderListViewModel: RemindersListViewModel
    // use fakeDataSourse to be injected in view model
    private lateinit var fakeDataSource: FakeDataSource

    private val reminder1 = ReminderDTO("Reminder1","Description1",
        "Location1",1.0,1.0,"1")
    private val reminder2 = ReminderDTO("Title2","Description2",
        "Location2",2.0,2.0,"2")
    private val reminder3 = ReminderDTO("Reminder3","Description3",
        "Location3",3.0,3.0,"3")
    // executes each  task synchronously using architecture components
    @get:Rule
    var instantExectuterRule = InstantTaskExecutorRule()
    // set main coroutine dispatchers for unit testing
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUpViewModel(){
        stopKoin()
        fakeDataSource = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
    }
    @After
    fun clearDataSource() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }
    @Test
    fun invalidateShowNoData_showNoData_isTrue() = runBlockingTest{
        //GIVEN - Empty DB
        fakeDataSource.deleteAllReminders()

        //WHEN -  load Reminders
        reminderListViewModel.loadReminders()

        //THEN - reminder list Live data size is 0 and show no data is true
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue().size, `is` (0))
        assertThat(reminderListViewModel.showNoData.getOrAwaitValue(), `is` (true))
    }
    @Test
    fun checkLoadReminders_loadsThreeReminders()= mainCoroutineRule.runBlockingTest {

        //GIVEN - reminder1,2,3
        fakeDataSource.deleteAllReminders()
        fakeDataSource.saveReminder(reminder1)
        fakeDataSource.saveReminder(reminder2)
        fakeDataSource.saveReminder(reminder3)
        //WHEN - load Reminders
        reminderListViewModel.loadReminders()
        //THEN -  3 reminders in remindersList and showNoData is false
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue().size, `is` (3))
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is` (false))

    }

    @Test
    fun loadReminders_checkLoading()= mainCoroutineRule.runBlockingTest{
        // Pause dispatcher to verify initial values
        mainCoroutineRule.pauseDispatcher()
        //GIVEN -  Reminder1
        fakeDataSource.deleteAllReminders()
        fakeDataSource.saveReminder(reminder1)

        //WHEN - load Reminders
        reminderListViewModel.loadReminders()
        //THEN - loading indicator is shown
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()
        // THEN - loading indicator is hidden
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(),`is`(false))

    }

    @Test
    fun loadReminders_shouldReturnError()= mainCoroutineRule.runBlockingTest{

        //GIVEN - SetshouldReturnError =  "true"
        fakeDataSource.setShouldReturnError(true)

        //WHEN - load Reminders
        reminderListViewModel.loadReminders()

        //THEN -  showSnackBar =  "There is exception"
        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(),`is`("There is exception"))

    }
}