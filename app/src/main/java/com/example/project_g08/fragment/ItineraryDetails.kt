package com.example.project_g08.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.project_g08.databinding.FragmentItineraryDetailsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ItineraryDetails : Fragment() {
    private var _binding: FragmentItineraryDetailsBinding? = null
    private val binding get() = _binding!!

    private  val args:ItineraryDetailsArgs by navArgs()

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItineraryDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.parkName.text = "NAME: ${args.ParkDataDb.parkName}"
        binding.parkAddress.text = "ADDRESS: ${args.ParkDataDb.address}"
        binding.button.setOnClickListener {
            val itineraryDetails = hashMapOf(
                "parkName" to "${args.ParkDataDb.parkName}",
                "parkAddress" to "${args.ParkDataDb.address}",
                "tripDate" to "${binding.editText.text}",
                "tripNotes" to "${binding.etNotes.text}"
            )
            db.collection("ItineraryDetails").add(itineraryDetails)
            Toast.makeText(requireContext(), "Data Saved", Toast.LENGTH_SHORT).show()
            val action = ItineraryDetailsDirections.actionItineraryDetailsToMyItinerary()
            findNavController().navigate(action)
        }
        db.collection("ItineraryDetails").get().addOnSuccessListener { documents ->
            var tripDate = ""
            var tripNotes = ""
            for (document in documents) {
                if (document.getString("parkName") == args.ParkDataDb.parkName) {
                    tripDate = document.getString("tripDate").toString()
                    tripNotes = document.getString("tripNotes").toString()
                }
            }
            binding.editText.setText(tripDate)
            binding.etNotes.setText(tripNotes)
        }.addOnFailureListener {
            Log.d("DocumentData", "ERROR while retrieving records")
        }
        binding.button2.setOnClickListener {
            db.collection("MyItinerary").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val parkName = document.getString("parkName")
                    if (parkName == args.ParkDataDb.parkName) {
                        db.collection("MyItinerary").document(document.id).delete().addOnSuccessListener {
                                Toast.makeText(requireContext(), "Trip Deleted", Toast.LENGTH_SHORT).show()
                                val action = ItineraryDetailsDirections.actionItineraryDetailsToMyItinerary()
                                findNavController().navigate(action)
                        }.addOnFailureListener { exception ->
                                Log.d("DocumentData", "ERROR while deleting record: ${exception.localizedMessage}")
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d("DocumentData", "ERROR while retrieving records")
            }
            db.collection("ItineraryDetails").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val parkName = document.getString("parkName")
                    if (parkName == args.ParkDataDb.parkName) {
                        db.collection("ItineraryDetails").document(document.id).delete()
                    }
                }
            }.addOnFailureListener {
                Log.d("DocumentData", "ERROR while retrieving records")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


