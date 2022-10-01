package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var database: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private val reminder1 = ReminderDTO(
        "Reminder1", "Description1",
        "Location1", 1.0, 1.0, "1"
    )
    private val reminder2 = ReminderDTO(
        "Reminder2", "Description2",
        "Location2", 2.0, 2.0, "2"
    )
    private val reminder3 = ReminderDTO(
        "Reminder3", "Description3",
        "Location3", 3.0, 3.0, "3"
    )

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_getReminderById() = runBlocking {
        //GIVEN - reminder1
        //WHEN - save reminder
        remindersLocalRepository.saveReminder(reminder1)
        // THEN - get reminder by id then check actual equals expect
        val reminder = remindersLocalRepository.getReminder(reminder1.id) as Result.Success

        assertThat(reminder.data.id, `is`(reminder1.id))
        assertThat(reminder.data.title, `is`(reminder1.title))
        assertThat(reminder.data.description, `is`(reminder1.description))
        assertThat(reminder.data.longitude, `is`(reminder1.longitude))
        assertThat(reminder.data.latitude, `is`(reminder1.latitude))
        assertThat(reminder.data.location, `is`(reminder1.location))
    }

    @Test
    fun saveReminders_getReminders() = runBlocking {
        //GIVEN - reminder1, reminder2, reminder3
        //WHEN - save reminders
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        // THEN - get reminders then check actual equals expect
        val reminder = remindersLocalRepository.getReminders() as Result.Success

        assertThat(reminder.data[0].id, `is`(reminder1.id))
        assertThat(reminder.data[0].title, `is`(reminder1.title))
        assertThat(reminder.data[0].description, `is`(reminder1.description))
        assertThat(reminder.data[0].longitude, `is`(reminder1.longitude))
        assertThat(reminder.data[0].latitude, `is`(reminder1.latitude))
        assertThat(reminder.data[0].location, `is`(reminder1.location))
        assertThat(reminder.data[1].id, `is`(reminder2.id))
        assertThat(reminder.data[1].title, `is`(reminder2.title))
        assertThat(reminder.data[1].description, `is`(reminder2.description))
        assertThat(reminder.data[1].longitude, `is`(reminder2.longitude))
        assertThat(reminder.data[1].latitude, `is`(reminder2.latitude))
        assertThat(reminder.data[1].location, `is`(reminder2.location))
        assertThat(reminder.data[2].id, `is`(reminder3.id))
        assertThat(reminder.data[2].title, `is`(reminder3.title))
        assertThat(reminder.data[2].description, `is`(reminder3.description))
        assertThat(reminder.data[2].longitude, `is`(reminder3.longitude))
        assertThat(reminder.data[2].latitude, `is`(reminder3.latitude))
        assertThat(reminder.data[2].location, `is`(reminder3.location))
        assertThat(reminder.data.size, `is`(3))
    }

    @Test
    fun saveReminders_deleteReminders() = runBlocking {
        //GIVEN - reminder1, reminder2, reminder3
        //WHEN - save reminders
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        // THEN - delete reminders then get reminders then check size of reminders are zero
        remindersLocalRepository.deleteAllReminders()
        val reminders = remindersLocalRepository.getReminders() as Result.Success
        assertThat(reminders.data.size, `is`(0))
    }

    @Test
    fun getReminder_returnError() = runBlocking {
        //GIVEN - empty DB
        remindersLocalRepository.deleteAllReminders()
        //WHEN - get reminder1
        val value = remindersLocalRepository.getReminder(reminder1.id) as Result.Error
        // THEN - check value with Reminder not found
        assertThat(value.message, `is`("Reminder not found!"))
    }

}