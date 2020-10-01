package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var fakeDataSource: FakeDataSource

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() = mainCoroutineRule.runBlockingTest {

        stopKoin()

        fakeDataSource = FakeDataSource()

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
    }


    @Test
    fun shouldReturnError() = mainCoroutineRule.runBlockingTest {
        val reminder =
            ReminderDataItem(
                "title",
                "description",
                "",
                0.0,
                0.0
            )

        //empty location
        assertThat(
            saveReminderViewModel.validateEnteredData(reminder), `is`(false)
        )

        assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location)
        )

    }

    @Test
    fun check_loading() = mainCoroutineRule.runBlockingTest {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Save reminder in viewmodel
        saveReminderViewModel.saveReminder(
            ReminderDataItem(
                "title",
                "description",
                "location",
                0.0,
                0.0
            )
        )

        // progress indicator is shown
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true)
        )

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // progress indicator is hidden
        assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false)
        )
    }
}