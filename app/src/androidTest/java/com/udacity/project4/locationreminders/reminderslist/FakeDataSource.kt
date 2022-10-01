package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
//Use FakeDataSource that act as test double to the localDataSource
class FakeDataSource(val reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {
    private var shouldReturnError = false
    fun setShouldReturnError(value: Boolean){
        shouldReturnError = value

    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError){
         return Result.Error("Reminders not Found")
        }
        reminders.let { return Result.Success(ArrayList(it)) }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return Result.Error("Reminders not Found")
        }
        val reminder = reminders?.find {
            it.id == id
        }
        return if (reminder != null){
            Result.Success(reminder)
        }else{
             Result.Error("Reminders not Found")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}