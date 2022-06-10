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
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var auth : FirebaseAuth
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
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerBtn.setOnClickListener {
            check()
        }

        var password = ""

        binding.passwordEditText.doOnTextChanged { text, start, before, count ->
           password = binding.passwordEditText.text.toString()
        }



        binding.passwordConfirmEditText.doOnTextChanged { text, start, before, count ->


            if(text != password && text!!.length >= password.length){
                binding.paswordConfirmInputLayout.error = "Parolalar aynı değil"
            }

        }

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

                if(currentUser != null){
                    currentUser.updateProfile(profilGuncellemesiIsteği).addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(context,"Profil Kullanıcı Adı Eklendi", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

}