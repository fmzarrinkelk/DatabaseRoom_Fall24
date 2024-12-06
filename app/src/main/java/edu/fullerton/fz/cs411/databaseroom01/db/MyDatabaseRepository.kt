package edu.fullerton.fz.cs411.databaseroom01.db

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import edu.fullerton.fz.cs411.databaseroom01.TAG
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "my-database"

class MyDatabaseRepository constructor(private val fragment: Fragment) {

    private val database: MyDatabase = Room.databaseBuilder(
        fragment.requireContext().applicationContext,
        MyDatabase::class.java,
        DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    private val myDao = database.myDao()

    private val executor = Executors.newSingleThreadExecutor()

    private var currentPersonIndex: Int = 0
    var currentPersonID: MutableLiveData<UUID?> = MutableLiveData<UUID?>()
    var currentPerson: LiveData<Person?> = MutableLiveData<Person?>()
    val personIDs: LiveData<List<UUID>> = this.fetchPersonIDs()

    fun fetchPersonIDs(): LiveData<List<UUID>> = myDao.fetchPersonIDs()
    fun fetchPersons(): LiveData<List<Person>> = myDao.fetchPersons()
    fun fetchPerson(id: UUID): LiveData<Person?> = myDao.fetchPerson(id)
    fun addPerson(person: Person) {
        this.executor.execute {
            myDao.addPerson(person)
        }
    }
    fun updatePerson(person: Person) {
        this.executor.execute {
            myDao.updatePerson(person)
        }
    }
    fun removePerson(id: UUID) {
        this.executor.execute {
            myDao.removePerson(id)
        }
    }

    private fun keepCurrentPersonIndexInBounds()
    {
        Log.v(TAG, "keepCurrentPersonIndexInBounds() - Start value: ${this.currentPersonIndex}")
        if ( this.personIDs.value == null ) {
            this.currentPersonIndex = 0
        }
        else {
            val ids = this.personIDs.value
            if ( this.currentPersonIndex < 0 ) {
                this.currentPersonIndex = ids!!.size - 1
            }
            else if ( this.currentPersonIndex >= ids!!.size ) {
                this.currentPersonIndex = 0
            }
        }
        Log.v(TAG, "keepCurrentPersonIndexInBounds() - End value: ${this.currentPersonIndex}")
    }
    fun previousPerson(): Unit
    {
        this.adjustPersonIndex(-1)
    }
    fun nextPerson(): Unit
    {
        this.adjustPersonIndex(1)
    }
    private fun adjustPersonIndex(adjustment: Int)
    {
        this.currentPersonIndex += adjustment
        this.keepCurrentPersonIndexInBounds()
        this.updateCurrentPersonID()
    }

    private fun updateCurrentPersonID() {
        Log.v(TAG, "updateCurrentPersonID")
        this.keepCurrentPersonIndexInBounds()
        this.personIDs.value?.let { list ->
            if ( this.currentPersonIndex >= 0 && this.currentPersonIndex < list.size ) {
                this.currentPersonID.value = list[this.currentPersonIndex]
                this.updateCurrentPerson()
            }
        }
    }

    private fun updateCurrentPerson() {
        Log.v(TAG, "updateCurrentPerson")
        this.currentPersonID.value?.let { pid ->
            this.currentPerson = myDao.fetchPerson(pid)
            this.currentPerson.observe(this.fragment.viewLifecycleOwner) { person ->
                Log.v(TAG, "Loaded person $person")
            }
        }
    }

    init {
        watchStuff()
    }

    private fun watchStuff() {
        this.personIDs.observe(this.fragment.viewLifecycleOwner) { ids ->
            Log.v(TAG, "Loaded person ID: $ids")
            this.updateCurrentPersonID()
            this.updateCurrentPerson()
        }
        this.currentPerson.observe(this.fragment.viewLifecycleOwner) { person ->
            Log.v(TAG, "Loaded person $person")
        }
    }
}