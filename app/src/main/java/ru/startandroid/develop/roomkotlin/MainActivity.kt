package ru.startandroid.develop.roomkotlin

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi
import ru.startandroid.develop.roomkotlin.databinding.ActivityMainBinding
import ru.startandroid.develop.roomkotlin.db.Subscriber
import ru.startandroid.develop.roomkotlin.db.SubscriberDatabase
import ru.startandroid.develop.roomkotlin.db.SubscriberRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, factory).get(SubscriberViewModel::class.java)
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this

        initRecyclerView()

        subscriberViewModel.message.observe(this, Observer { it.getContentIfNotHandled()?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() } })

    }

    private fun initRecyclerView(){

        binding.subsriberRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({selectedItem:Subscriber->listItemClicked(selectedItem)})
        binding.subsriberRecyclerView.adapter = adapter
        displaySubsribersList()
    }



    private fun displaySubsribersList(){
        subscriberViewModel.subscribers.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()

        })
    }

    private fun listItemClicked(subscriber:Subscriber){
        //Toast.makeText(this, "selected name is ${subscriber.name}", Toast.LENGTH_LONG).show()
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}