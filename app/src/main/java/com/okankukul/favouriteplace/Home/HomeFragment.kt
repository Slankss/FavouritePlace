package com.okankukul.favouriteplace.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.okankukul.favouriteplace.Adapter.RecylerAdapter
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.ActivityHomeBinding
import com.okankukul.favouriteplace.databinding.FragmentHomeBinding
import com.okankukul.favouriteplace.databinding.FragmentLoginBinding

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var recyclerViewAdapter : RecylerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        getData(view)

        println("home fragment ındayız")

        for (item in postList){
            println(item.placeName)
        }

        val layoutManager = LinearLayoutManager(view.context)
        binding.recylerView.layoutManager = layoutManager
        recyclerViewAdapter= RecylerAdapter(postList,view.context)
        binding.recylerView.adapter = recyclerViewAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    fun getData(view : View){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("Post").orderBy("date", Query.Direction.DESCENDING


        ).addSnapshotListener { snapshot, exception ->
            if(exception != null)
            {
                Toast.makeText(view.context,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else
            {
                if(snapshot != null)
                {
                    if(!snapshot.isEmpty) // boş değilse
                    {
                        val documents = snapshot.documents

                        postList.clear()
                        for(document in documents)
                        {
                            var userEmail = document.get("userEmail") as String
                            var username = document.get("userName") as String
                            var placeAdress = document.get("location") as String
                            var placeName = document.get("placeName") as String
                            var imageUrl = document.get("imageUrl") as String


                            val  indirilenPost= Post(placeName,placeAdress,username,userEmail,imageUrl)
                            postList.add(indirilenPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged() // verileri yenile demek
                    }
                }
            }
        }

    }


}