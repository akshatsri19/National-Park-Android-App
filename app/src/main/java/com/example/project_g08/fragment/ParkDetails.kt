package com.example.project_g08.fragment

import android.os.Bundle
import android.text.util.Linkify
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.project_g08.databinding.FragmentParkDetailsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ParkDetails : Fragment() {

    private var _binding: FragmentParkDetailsBinding? = null
    private val binding get() = _binding!!

    private val args:ParkDetailsArgs by navArgs()

    private val db = Firebase.firestore.collection("MyItinerary")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text = "${args.ParkData.fullName}"

        val parkImage = args.ParkData.images[0]
        Glide.with(this)
            .load(parkImage.url)
            .into(binding.ivPhoto)

        val address = args.ParkData.addresses[0]
        binding.textView3.text = "${address.line1}, ${address.city}, ${address.postalCode}"

        binding.textView4.autoLinkMask = Linkify.WEB_URLS
        binding.textView4.text = args.ParkData.url

        binding.textView6.text = "${args.ParkData.description}"

        binding.btnToSavePark.setOnClickListener {
            val address = args.ParkData.addresses[0]
            val parkData = hashMapOf(
                "parkName" to args.ParkData.fullName,
                "parkAddress" to "${address.line1}, ${address.city}, ${address.postalCode}"
            )
            db.add(parkData)
            Toast.makeText(requireContext(),"Park Added",Toast.LENGTH_SHORT).show()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}