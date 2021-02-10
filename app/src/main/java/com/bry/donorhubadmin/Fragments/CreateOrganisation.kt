package com.bry.donorhubadmin.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import com.bry.donorhubadmin.R


class CreateOrganisation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var listener: CreateOrganisationInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CreateOrganisationInterface){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_create_organisation, container, false)
        val nameEditText: EditText = va.findViewById(R.id.nameEditText)
        val locationEditText: EditText = va.findViewById(R.id.locationEditText)
        val continue_layout: RelativeLayout = va.findViewById(R.id.continue_layout)

        continue_layout.setOnClickListener {
            val n = nameEditText.text.toString().trim()
            val l = locationEditText.text.toString().trim()

            if(n.equals("")){
                nameEditText.setError("Type something")
            }else if(l.equals("")){
                locationEditText.setError("Type something")
            }else{
                listener.whenCreateOrganisationContinue(n,l)
            }

        }

        return va
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateOrganisation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    interface CreateOrganisationInterface{
        fun whenCreateOrganisationContinue(org_name: String, org_location: String)
    }
}