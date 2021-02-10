package com.bry.donorhubadmin.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bry.donorhubadmin.R


class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val ARG_PARAM1 = "param1"
    private val ARG_PARAM2 = "param2"
    private lateinit var listener: HomeInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeInterface){
            listener = context
        }
    }

    var when_data_updated: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val va = inflater.inflate(R.layout.fragment_home, container, false)
        val create_org: RelativeLayout = va.findViewById(R.id.create_org)
        val view_org: RelativeLayout = va.findViewById(R.id.view_org)
        val view_donations: RelativeLayout = va.findViewById(R.id.view_donations)
        val swipeContainer: SwipeRefreshLayout = va.findViewById(R.id.swipeContainer)

        create_org.setOnClickListener {
            listener.whenHomeCreateOrganisation()
        }

        view_org.setOnClickListener {
            listener.whenHomeViewOrganisation()
        }

        view_donations.setOnClickListener {
            listener.whenHomeViewDonations()
        }

        swipeContainer.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                listener.whenReloadEverything()
            }
        })

        when_data_updated = {
            swipeContainer.setRefreshing(false)
        }

        return va
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    interface HomeInterface{
        fun whenHomeCreateOrganisation()
        fun whenHomeViewOrganisation()
        fun whenHomeViewDonations()
        fun whenReloadEverything()
    }
}