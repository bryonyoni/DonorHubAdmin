package com.bry.donorhubadmin.Activities

import android.animation.ValueAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.bry.donorhubadmin.Constants
import com.bry.donorhubadmin.Fragments.*
import com.bry.donorhubadmin.Model.Donation
import com.bry.donorhubadmin.Model.Organisation
import com.bry.donorhubadmin.R
import com.bry.donorhubadmin.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(),
        Home.HomeInterface,
        CreateOrganisation.CreateOrganisationInterface,
        ViewOrganisations.ViewOrganisationsInterface,
        ViewOrganisation.ViewOrganisationInterface,
        ViewDonations.ViewDonationsInterface,
        ViewDonation.ViewDonationInterface,
        AddOrganisationMember.AddOrganisationMemberInterface
{
    val TAG = "MainActivity"
    val constants = Constants()

    val _home = "_home"
    val _create_organisation = "_create_organisation"
    val _view_organisation = "_view_organisation"
    val _view_organisations = "_view_organisations"
    val _view_donations = "_view_donations"
    val _view_donation = "_view_donation"
    val _view_image = "_view_image"
    val _add_organisation_member = "_add_organisation_member"

    private lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore

    private var donations: ArrayList<Donation> = ArrayList()
    private var organisations: ArrayList<Organisation> = ArrayList()
    var doubleBackToExitPressedOnce: Boolean = false
    var is_loading: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar = supportActionBar!!
        actionBar.hide()

        loadDonationsAndOrganisations()
        openHomepage()
    }

    fun openHomepage(){
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(binding.money.id, Home.newInstance("",""),_home).commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size > 1) {
            val trans = supportFragmentManager.beginTransaction()
            trans.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
            val currentFragPos = supportFragmentManager.fragments.size - 1

            trans.remove(supportFragmentManager.fragments.get(currentFragPos))
            trans.commit()
            supportFragmentManager.popBackStack()

        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    fun hideLoadingScreen(){
        is_loading = false
        binding.loadingScreen.visibility = View.GONE
    }

    fun showLoadingScreen(){
        is_loading = true
        binding.loadingScreen.visibility = View.VISIBLE
        binding.loadingScreen.setOnTouchListener { v, _ -> true }

    }



    fun loadDonationsAndOrganisations(){
        showLoadingScreen()
        organisations.clear()
        donations.clear()

        db.collection("organisations").get().addOnSuccessListener {
            if(!it.isEmpty){
                for(doc in it.documents){
                    if(doc.contains("org_obj")){
                        val org = Gson().fromJson(doc["org_obj"] as String, Organisation::class.java)
                        organisations.add(org)
                    }
                }
            }
        }

        db.collection("donations").get().addOnSuccessListener {
            if(!it.isEmpty){
                for(doc in it.documents){
//                    if(doc.contains("don_obj")){
//                        val don = Gson().fromJson(doc["don_obj"] as String, Donation::class.java)
//                        if(doc.contains("taken_down")){
//                            don.is_taken_down = doc["taken_down"] as Boolean
//                        }
//                        donations.add(don)
//                    }
                }
            }
            hideLoadingScreen()
            Toast.makeText(applicationContext, "Done loading!", Toast.LENGTH_SHORT).show()
        }
    }




    override fun whenHomeCreateOrganisation() {
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, CreateOrganisation.newInstance("",""),_create_organisation).commit()
    }

    override fun whenHomeViewOrganisation() {
        val orgs = Gson().toJson(Organisation.organisation_list(organisations))
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, ViewOrganisations.newInstance("","",orgs),_view_organisations)
            .commit()
    }

    override fun whenHomeViewDonations() {
        if(organisations.isNotEmpty()) {
            val org = Gson().toJson(organisations[0])
            val dons = Gson().toJson(Donation.donation_list(donations))
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .add(binding.money.id,
                    ViewDonations.newInstance("", "", dons, org),
                    _view_donations
                )
                .commit()
        }else{
            Toast.makeText(applicationContext, "no organisations!", Toast.LENGTH_SHORT).show()
        }
    }




    override fun whenReloadEverything() {
        loadDonationsAndOrganisations()
    }




    override fun whenCreateOrganisationContinue(org_name: String, org_location: String) {
        showLoadingScreen()
        val time = Calendar.getInstance().timeInMillis

        val ref = db.collection("organisations")
            .document()

        val new_org = Organisation(org_name, time, "Nairobi", ref.id)

        val data = hashMapOf(
            "time_of_creation" to time,
            "name" to org_name,
            "org_id" to ref.id,
            "org_obj" to Gson().toJson(new_org)
        )

        organisations.add(new_org)

        ref.set(data).addOnSuccessListener {
            hideLoadingScreen()
            Toast.makeText(applicationContext,"done!", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    override fun whenPickOrganisationOrgPicked(organisation: Organisation) {
        val org = Gson().toJson(organisation)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, ViewOrganisation.newInstance("","",org),_view_organisation)
            .commit()
    }

    override fun whenViewOrganisationViewDonations(organisation: Organisation) {
        val list: ArrayList<Donation> = ArrayList()

        for(item in donations){
            if(item.organisation_id.equals(organisation.org_id)){
                list.add(item)
            }
        }

        val org = Gson().toJson(organisation)
        val dons = Gson().toJson(Donation.donation_list(list))
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, ViewDonations.newInstance("","",dons,org),_view_donations)
            .commit()

    }

    override fun whenNewDonationAddMember(organisation: Organisation) {
        val orgs = Gson().toJson(organisation)

        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .add(binding.money.id, AddOrganisationMember.newInstance("", "", orgs)
                        , _add_organisation_member).commit()
    }

    override fun whenNewDonationViewDonation(donation: Donation, organisation: Organisation) {
        val don = Gson().toJson(donation)
        val org = Gson().toJson(organisation)
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, ViewDonation.newInstance("","", org, don),_view_donation)
            .commit()
    }




    override fun whenScheduleDonationTakeDownClicked(
        donation: Donation,
        organisation: Organisation,
        take_down: Boolean
    ) {
        showLoadingScreen()

        db.collection("donations")
            .document(donation.donation_id)
            .update(mapOf(
                "taken_down" to take_down
            )).addOnSuccessListener {
                hideLoadingScreen()
                Toast.makeText(applicationContext, "donation updated", Toast.LENGTH_SHORT).show()
            }
    }

    override fun whenViewDonationImage(image: Donation.donation_image, donation: Donation) {
        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .add(binding.money.id, ViewImage.newInstance("", "", Gson().toJson(image)
                , Gson().toJson(donation)), _view_image).commit()
    }

    override fun generatePasscodeClicked(organisation: Organisation, code: Long) {
        showLoadingScreen()
        db.collection(constants.otp_codes)
                .document(organisation.org_id)
                .collection(constants.code_instances)
                .document().set(hashMapOf(
                        "code" to code,
                        "organisation" to organisation.org_id,
                        "creation_time" to Calendar.getInstance().timeInMillis
                )).addOnSuccessListener {
                    hideLoadingScreen()
                    Toast.makeText(applicationContext,"The password will only work for 1 min",Toast.LENGTH_SHORT).show()
                    if(supportFragmentManager.findFragmentByTag(_add_organisation_member)!=null){
                        (supportFragmentManager.findFragmentByTag(_add_organisation_member) as AddOrganisationMember).isPasscodeSet()
                    }
                }
    }


}