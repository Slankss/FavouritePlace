package com.okankukul.favouriteplace.Login_Register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private  var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private var email = ""
    private var password = ""
    private var password_again = ""
    private var username = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {
            check()
        }

        var password = ""




    }

    private fun check(){

        email = binding.emailEditText.text.toString().trim()
        username = binding.usernameEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString().trim()
        password_again = binding.passwordConfirmEditText.text.toString().trim()

        if(email != "" && username != ""&& password != "" && password_again != "" ){

            if(password == password_again){
                register()
            }
            else{ binding.paswordConfirmInputLayout.error = "Parolalar aynı değil" }
        }
        else{

            if(!email.isEmpty()){ binding.emailInputLayout.error = null }
            else{ binding.emailInputLayout.error = "Bu alan boş olamaz!" }

            if(!username.isEmpty()){ binding.usernameInputLayout.error = null }
            else{ binding.usernameInputLayout.error = "Bu alan boş olamaz!" }
            if (!password.isEmpty()){ binding.paswordInputLayout.error = null }
            else{ binding.paswordInputLayout.error = "Bu alan boş olamaz!" }
            if(!password_again.isEmpty()){ binding.paswordConfirmInputLayout.error = null }
            else{ binding.paswordConfirmInputLayout.error = "Bu alan boş olamaz!" }

            Toast.makeText(context,"Boş Değerler var", Toast.LENGTH_LONG).show()
        }
    }

    private fun register(){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val currentUser =auth.currentUser

                val profilGuncellemesiIsteği = userProfileChangeRequest {
                    displayName = username
                }

                val profileHashMap = hashMapOf<String,Any>()
                var friendList = arrayListOf<String>()
                profileHashMap.put("email",email)
                profileHashMap.put("username",username)
                profileHashMap.put("password",password)
                profileHashMap.put("friends",friendList)

                fireStore.collection("Profile").add(profileHashMap).addOnCompleteListener { task ->
                    if(task.isSuccessful){

                    }

                }.addOnFailureListener { exception ->
                    println(exception.localizedMessage)
                }

                if(currentUser != null){
                    currentUser.updateProfile(profilGuncellemesiIsteği).addOnCompleteListener { task ->
                        if(task.isSuccessful){

                        }
                    }
                }
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }.addOnFailureListener { exception ->
            println(exception.localizedMessage)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}