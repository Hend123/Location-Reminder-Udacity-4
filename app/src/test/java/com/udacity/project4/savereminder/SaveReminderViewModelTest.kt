package com.udacity.project4.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.R
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import com.udacity.project4.locationreminders.data.dto.Result


//test SaveReminderViewModelTest with fake data store
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    // use fakeDataSourse to be injected in view model
    private lateinit var fakeDataSource: FakeDataSource

    private val reminder1 = ReminderDataItem("Reminder1","Description1",
        "Location1",1.0,1.0,"1")
    private val reminder2_noTitle = ReminderDataItem("","Description2",
        "Location2",2.0,2.0,"2")
    private val reminder3_noLocation = ReminderDataItem("Reminder3","Description3",
        "",3.0,3.0,"3")
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
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),fakeDataSource)
    }
    //test reminders are null after clear them by this fun
    @Test
    fun onClear_null(){
        //given
        saveReminderViewModel.reminderTitle.value = "Title"
        saveReminderViewModel.reminderDescription.value = "Description"
        saveReminderViewModel.reminderSelectedLocationStr.value = "Location"
        saveReminderViewModel.latitude.value = 1.0
        saveReminderViewModel.longitude.value = 1.0
        // when clearing reminder
        saveReminderViewModel.onClear()

        //then check that  actual equals expected are null
        assertThat(saveReminderViewModel.reminderTitle.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderDescription.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.latitude.value, `is`(nullValue()))
        assertThat(saveReminderViewModel.longitude.value, `is`(nullValue()))
    }
    //test reminder is saved in data store
    @Test
    fun saveReminder_reminder_existInDataStore() = mainCoroutineRule.runBlockingTest{
        //given is reminder1
        //when save reminder
        saveReminderViewModel.saveReminder(reminder1)
        //then check reminder exist in datastore or not
        fakeDataSource.setShouldReturnError(false)
        val value = fakeDataSource.getReminder("1") as Result.Success

        assertThat(value.data.id, `is`(reminder1.id))
        assertThat(value.data.title, `is`(reminder1.title))
        assertThat(value.data.description, `is`(reminder1.description))
        assertThat(value.data.location, `is`(reminder1.location))
        assertThat(value.data.latitude, `is`(reminder1.latitude))
        assertThat(value.data.longitude, `is`(reminder1.longitude))
    }
    // test showLoading works well when we save reminder
    @Test
    fun saveReminder_checkingLoading() = mainCoroutineRule.runBlockingTest {
        //pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()
        //given is reminder1
        //when save reminder
        saveReminderViewModel.saveReminder(reminder1)
        //then loading indicator is true means shown
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),`is`(true))
        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()
        //then loading indicator is false means hidden
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),`is`(false))

    }

    //test validateEnteredData with reminderNoTitle
    @Test
    fun validateEnteredData_reminderNoTitle_trueOrFalse(){
        // given reminder2_noTitle
        //when validate reminder
      val validate =  saveReminderViewModel.validateEnteredData(reminder2_noTitle)
        //then check showSnackBarInt equals Please enter title
        //and validate = false
        val valueSnackBar = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(valueSnackBar, `is`(R.string.err_enter_title))
        assertThat(validate,`is`(false))
    }
    //test validateEnteredData with reminderNoLocation
    @Test
    fun validateEnteredData_reminderNoLocation_trueOrFalse(){
        // given reminder3_noLocation
        //when validate reminder
        val validate =  saveReminderViewModel.validateEnteredData(reminder3_noLocation)
        //then check showSnackBarInt equals Please select location
        //and validate = false
        val valueSnackBar = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        assertThat(valueSnackBar, `is`(R.string.err_select_location))
        assertThat(validate,`is`(false))
    }

}