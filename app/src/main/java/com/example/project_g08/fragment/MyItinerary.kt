package com.example.project_g08.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.project_g08.adapter.ParkAdapter
import com.example.project_g08.databinding.FragmentMyItineraryBinding
import com.example.project_g08.model.ParkDataItemFromDb
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MyItinerary : Fragment() {
    private var _binding: FragmentMyItineraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var parkAdapter: ParkAdapter

    private val db = Firebase.firestore.collection("MyItinerary")

    private val parkList = mutableListOf<ParkDataItemFromDb>()
    private var parkNameList = mutableListOf<String?>()
    private var parkDataToSend = ParkDataItemFromDb("","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyItineraryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db.get().addOnSuccessListener { documents ->
            parkAdapter = ParkAdapter(requireContext(),parkList)
            binding.lvParks.adapter = parkAdapter
            for(document in documents){
                val parkName = document.getString("parkName")
                val parkAddress = document.getString("parkAddress")
                parkList.add(ParkDataItemFromDb(parkName,parkAddress))
                parkNameList.add(parkName)
            }
            parkAdapter.notifyDataSetChanged()
        }.addOnFailureListener{
            Log.d("DocumentData","ERROR while retrieving records")
        }

        binding.lvParks.setOnItemClickListener{ adapterView, view, position, id ->
            for(p in parkList){
                if(p.parkName == parkNameList[position]){
                    parkDataToSend = p
                }
            }
            Toast.makeText(requireContext(),"Itinerary Selected",Toast.LENGTH_SHORT).show()
        }

        binding.selectItinerary.setOnClickListener {
            if(parkDataToSend.parkName==""){
                Toast.makeText(requireContext(),"Please Select a itinerary",Toast.LENGTH_SHORT).show()
            }
            else{
                val action = MyItineraryDirections.actionMyItineraryToItineraryDetails(parkDataToSend)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

