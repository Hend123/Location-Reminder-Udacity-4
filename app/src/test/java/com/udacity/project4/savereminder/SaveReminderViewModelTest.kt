package com.udacity.project4.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.content.pm.ApplicationInfoBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

//test SaveReminderViewModelTest with fake data
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

    }
}