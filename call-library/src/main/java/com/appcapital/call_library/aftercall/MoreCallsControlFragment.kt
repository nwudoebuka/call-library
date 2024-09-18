package com.appcapital.call_library.aftercall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appcapital.call_library.R
import com.appcapital.call_library.aftercall.adapter.MessageControlsAdapter
import com.appcapital.call_library.databinding.FragmentMoreCallsControlBinding
import com.appcapital.call_library.databinding.FragmentWeatherCardBinding
import com.appcapital.call_library.utils.SharedPreferencesHelper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoreCallsControlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoreCallsControlFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentMoreCallsControlBinding? = null
    var selectedQuickMessage = ""
    private val binding get() = _binding!!
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WEATHERCARD","onCreate");
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMoreCallsControlBinding.inflate(inflater, container, false)
        val view = binding.root

        val recyclerView: RecyclerView = view.findViewById(R.id.rec)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val quickMessageOptions = listOf(getString(R.string.I_am_busy), getString(R.string.let_me_call_back), getString(R.string.in_a_meeting), getString(R.string.cant_talk))
        val adapter = MessageControlsAdapter(quickMessageOptions) {
            selectedQuickMessage = quickMessageOptions.get(it)
        }
        recyclerView.adapter = adapter
        binding.sendMessage.setOnClickListener {
            if(selectedQuickMessage.isEmpty()){
               Toast.makeText(requireContext(),getString(R.string.select_quick_message),Toast.LENGTH_LONG).show()
            }else{
                val phoneNumber = SharedPreferencesHelper.getCalledPhoneNumber(requireContext())
                val message = selectedQuickMessage

                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phoneNumber")
                    putExtra("sms_body", message)
                }
                startActivity(smsIntent)
            }
        }
        return  view
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MoreCallsControlFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MoreCallsControlFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}