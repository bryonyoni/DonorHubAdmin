package com.bry.donorhubadmin.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bry.donorhubadmin.Constants
import com.bry.donorhubadmin.Model.Organisation
import com.bry.donorhubadmin.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson


class ViewOrganisation : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private val ARG_ORGAN = "ARG_ORGAN"
    private lateinit var listener: ViewOrganisationInterface
    private lateinit var organisation: Organisation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            organisation = Gson().fromJson(it.getString(ARG_ORGAN), Organisation::class.java)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ViewOrganisationInterface){
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_view_organisation, container, false)
        val organisation_name: TextView = va.findViewById(R.id.organisation_name)
        val location: TextView = va.findViewById(R.id.location)
        val view_donations_relative: RelativeLayout = va.findViewById(R.id.view_donations_relative)
        val add_organisation_relative: RelativeLayout = va.findViewById(R.id.add_organisation_relative)

        val org_image: ImageView = va.findViewById(R.id.org_image)

        organisation_name.text = organisation.name
        location.text = organisation.location_name

        view_donations_relative.setOnClickListener {
            listener.whenViewOrganisationViewDonations(organisation)
        }

        add_organisation_relative.setOnClickListener {
            listener.whenNewDonationAddMember(organisation)
        }

        val d = organisation.org_id
        val storageReference = Firebase.storage.reference
            .child("organisation_backgrounds")
            .child("${d}.jpg")

        Constants().load_normal_job_image(storageReference, org_image, context!!)

        return va
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String, ogr: String) =
            ViewOrganisation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_ORGAN, ogr)
                }
            }
    }

    interface ViewOrganisationInterface{
        fun whenViewOrganisationViewDonations(organisation: Organisation)
        fun whenNewDonationAddMember(organisation: Organisation)
    }
}