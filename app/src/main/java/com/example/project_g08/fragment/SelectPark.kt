package com.example.project_g08.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_g08.R
import com.example.project_g08.databinding.FragmentSelectParkBinding
import com.example.project_g08.model.Address
import com.example.project_g08.model.MyResObj
import com.example.project_g08.model.Park
import com.example.project_g08.model.StateData
import com.example.project_g08.network.ApiService
import com.example.project_g08.network.RetrofitInstance
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import retrofit2.Response


class SelectPark : Fragment(R.layout.fragment_select_park), OnMapReadyCallback {

    // For View Binding
    private var _binding:FragmentSelectParkBinding? = null
    private val binding get() = _binding!!

    // Map Variable
    private lateinit var mMap:GoogleMap

    // Data list variables for getting state name and state data from the database also for sending the state code to the api
    private val stateDataList = mutableListOf<StateData>()
    private val stateNameList = mutableListOf<String>()
    private var stateCodeToSend = ""

    // Setting up database variable
    private val db = Firebase.firestore.collection("StateData")

    // Setting up the adapter variable
    private lateinit var myArrayAdapter: ArrayAdapter<String>

    // Setting up coordinates
    private var coordinatesList = mutableListOf<LatLng>()
    private var parkNameList = mutableListOf<String>()
    private var parkAddressList = mutableListOf<Address>()
    private var selectedPark:Park? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectParkBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting Data from Db for state code and setting up the adapter view for the spinner
        db.get().addOnSuccessListener { documents ->
            for(document in documents){
                val stateName = document.getString("stateName")
                val stateCode = document.getString("stateCode")
                stateDataList.add(StateData(stateName!!,stateCode!!))
                stateNameList.add(stateName)
            }
            this.myArrayAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_dropdown_item,stateNameList)
            binding.spinner.adapter = myArrayAdapter
            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    for (s in stateDataList) {
                        if (stateNameList[p2] == s.stateName) {
                            stateCodeToSend = s.stateCode
                        }
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    binding.spinner.setSelection(0)
                }
            }
            binding.findParks.setOnClickListener {
                lifecycleScope.launch {
                    val resObj = getParkFromAPI(stateCodeToSend) ?: return@launch
                    coordinatesList.clear()
                    parkNameList.clear()
                    parkAddressList.clear()
                    val parkList = resObj.data
                    for(park in parkList){
                        coordinatesList.add(LatLng(park.latitude,park.longitude))
                        parkNameList.add(park.fullName)
                        parkAddressList.add(park.addresses[0])
                    }
                    mMap.clear()
                    for ((index,currCoordinates) in coordinatesList.withIndex()){
                        mMap.addMarker(MarkerOptions().position(currCoordinates).title(parkNameList[index])
                            .snippet("${parkAddressList[index].line1}, ${parkAddressList[index].postalCode}, ${parkAddressList[index].city}"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currCoordinates,5.0f))
                    }
                    mMap.setOnMarkerClickListener {marker ->
                        selectedPark = parkList.find { it.fullName == marker.title }
                        false
                    }
                }
            }
            binding.selectPark.setOnClickListener {
                if(selectedPark == null){
                    Toast.makeText(requireContext(),"Please select a park",Toast.LENGTH_SHORT).show()
                }
                else{
                    val action = SelectParkDirections.actionSelectParkToParkDetails(selectedPark!!)
                    findNavController().navigate(action)
                }
            }
        }.addOnFailureListener{
            Log.d("DocumentData","ERROR while retrieving records")
        }



        // Setting up the map with all the attributes
        val mapFragment = childFragmentManager.findFragmentById(binding.fragmentMap.id) as? SupportMapFragment

        if (mapFragment == null) {
            Log.d("MAP", "++++ map fragment is null")
        }
        else {
            mapFragment?.getMapAsync(this)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    // Map function
    override fun onMapReady(googleMap: GoogleMap) {
        this.mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.isTrafficEnabled = true
        val uiSettings = googleMap.uiSettings
        uiSettings.isZoomControlsEnabled = true
        uiSettings.isCompassEnabled = true
    }

    // Getting data from API function
    private suspend fun getParkFromAPI(stateCode:String): MyResObj? {
        // getting a reference to the interface
        var apiService: ApiService = RetrofitInstance.retrofitService
        val response: Response<MyResObj> = apiService.getPark(stateCode,"0tm4XBfzdhj19nwRv7R68L24GiSI2JMeyeIfVDEm")

        if (response.isSuccessful) {
            val dataFromAPI = response.body()
            if (dataFromAPI == null) {
                Log.d("API", "No data from API or some other error")
                return null
            }

            // if you reach this point, then you must have received a response from the api
            Log.d("API", "Here is the data from the API")
            Log.d("API", dataFromAPI.toString())
            return dataFromAPI
        }
        else {
            // Handle error
            Log.d("API", "An error occurred")
            return null
        }
    }
}