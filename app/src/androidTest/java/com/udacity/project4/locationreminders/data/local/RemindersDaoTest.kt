package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase
    private val reminder1 = ReminderDTO("Reminder1","Description1",
        "Location1",1.0,1.0,"1")
    private val reminder2 = ReminderDTO("Reminder2","Description2",
        "Location2",2.0,2.0,"2")
    private val reminder3 = ReminderDTO("Reminder3","Description3",
        "Location3",3.0,3.0,"3")

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

    }

    @After
    fun closeDb() = database.close()
// test getReminders
    @Test
    fun insertReminderAndGetAll() = runBlockingTest {
        // GIVEN - Insert  reminders.
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Get the reminders from database
        val reminders = database.reminderDao().getReminders()

        // THEN - The loaded data contains the expected values.
        assertThat(reminders[0].id, `is`(reminder1.id))
        assertThat(reminders[0].title, `is`(reminder1.title))
        assertThat(reminders[1].id, `is`(reminder2.id))
        assertThat(reminders[1].title, `is`(reminder2.title))
        assertThat(reminders[2].id, `is`(reminder3.id))
        assertThat(reminders[2].title, `is`(reminder3.title))
        assertThat(reminders.size, `is`(3))

    }
    // test getReminderById
    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        // GIVEN - Insert a reminder.
        database.reminderDao().saveReminder(reminder1)
        // WHEN - Get the task by id from the database.
        val reminder = database.reminderDao().getReminderById(reminder1.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(reminder as ReminderDTO, notNullValue())
        assertThat(reminder.id, `is`(reminder1.id))
        assertThat(reminder.title, `is`(reminder1.title))
        assertThat(reminder.description, `is`(reminder1.description))
        assertThat(reminder.longitude, `is`(reminder1.longitude))
        assertThat(reminder.latitude, `is`(reminder1.latitude))
        assertThat(reminder.location, `is`(reminder1.location))
    }
    // test deleteAllReminders
    @Test
    fun insertReminderAndDeleteAll() = runBlockingTest {
        // GIVEN - Insert  reminders.
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        // WHEN - Delete the reminders from database
        database.reminderDao().deleteAllReminders()


        // THEN - The loaded data size equals zero
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.size, `is`(0))
    }

    @Test
    fun getReminder_returnError() = runBlocking {
        //GIVEN - empty DB
        database.reminderDao().deleteAllReminders()
        //WHEN - get reminder1
        val value = database.reminderDao().getReminderById(reminder1.id)
        // THEN - check value with null
        assertThat(value, `is`(nullValue()))
    }

}