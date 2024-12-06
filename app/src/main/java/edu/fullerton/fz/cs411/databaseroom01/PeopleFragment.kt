package edu.fullerton.fz.cs411.databaseroom01

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import edu.fullerton.fz.cs411.databaseroom01.db.MyDatabaseRepository
import edu.fullerton.fz.cs411.databaseroom01.db.Person
import java.lang.Exception

class PeopleFragment: Fragment() {

    private lateinit var dbRepo: MyDatabaseRepository

    private lateinit var createButton: Button
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var personName: EditText
    private lateinit var personAge: EditText
    private lateinit var personFavoriteColor: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.v(TAG, "People fragment: onCreateView()")

        val view = inflater.inflate(R.layout.fragment_people, container, false)

        this.personName = view.findViewById(R.id.input_person_name)
        this.personAge = view.findViewById(R.id.input_person_age)
        this.personFavoriteColor = view.findViewById(R.id.input_person_favorite_color)

        this.prevButton = view.findViewById(R.id.button_prev_person)
        this.createButton = view.findViewById(R.id.button_create_person)
        this.updateButton = view.findViewById(R.id.button_update_person)
        this.deleteButton = view.findViewById(R.id.button_delete_person)
        this.nextButton = view.findViewById(R.id.button_next_person)

        this.dbRepo = MyDatabaseRepository(this)

        this.setupCallbacks()
        this.setObservers()

        return view
    }

    private fun setupCallbacks() {
        this.prevButton.setOnClickListener {
            this.dbRepo.previousPerson()
        }
        this.nextButton.setOnClickListener {
            this.dbRepo.nextPerson()
        }
        this.createButton.setOnClickListener {
            val p = this.viewsToPerson()
            Log.v(TAG, "Creating a person: $p")
            this.dbRepo.addPerson(p)
        }
        this.updateButton.setOnClickListener {
            this.dbRepo.currentPersonID.value?.let { id ->
                val p = this.viewsToPerson()
                p.id = id
                Log.v(TAG, "Updating a person to: $p")
                this.dbRepo.updatePerson(p)
            }
        }
        this.deleteButton.setOnClickListener {
            Log.v(TAG, "Deleting current person")
            this.dbRepo.currentPersonID.value?.let { id ->
                this.dbRepo.removePerson(id)
            }
        }
    }

    private fun viewsToPerson(): Person
    {
        val p = Person()

        p.name = this.personName.text.toString()
        try { p.age = this.personAge.text.toString().toInt() }
        catch(e: Exception) { p.age = 0 }
        p.color = this.personFavoriteColor.text.toString()

        Log.v(TAG, "viewsToPerson() - $p")

        return p
    }
    private fun personToViews(person: Person?)
    {
        if ( person == null ) {
            this.personName.setText("")
            this.personAge.setText("")
            this.personFavoriteColor.setText("")
        } else {
            this.personName.setText(person.name)
            this.personAge.setText(person.age.toString())
            this.personFavoriteColor.setText(person.color)
        }
    }

    private fun setObservers() {
        this.dbRepo.currentPersonID.observe(this.viewLifecycleOwner) { cpid ->
            Log.v(TAG, "the current person changed and its id is: $cpid")
            if (cpid == null) {
                this.personToViews(null)
            }
            cpid?.let { id ->
                val personLiveData = this.dbRepo.fetchPerson(id)
                personLiveData.observe(this.viewLifecycleOwner) { person ->
                    Log.v(TAG,"New person: $person")
                    this.personToViews(person)
                }
            }
        }
    }

}