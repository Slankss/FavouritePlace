package com.okankukul.favouriteplace.Login_Register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.okankukul.favouriteplace.Home.HomeActivity
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth : FirebaseAuth
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var currentUser = auth.currentUser
        if(currentUser !=null){
            activity?.let {
                startActivity(Intent(it.applicationContext, HomeActivity::class.java))
                it.finish()
            }
        }

        binding.goRegisterPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener {
            login()
        }

    }

    private fun login( ){

        email = binding.emailEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString().trim()

        if(email != "" && password != ""){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    activity?.let {
                        startActivity(Intent(it.applicationContext, HomeActivity::class.java))
                        it.finish()
                    }

                }
                else{
                    binding.passwordInputLayout.error = null
                    binding.emailInputLayout.error = null
                }

            }.addOnFailureListener { exception ->

                Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }

        }
        else{

            if(email.isEmpty()){ binding.emailInputLayout.error = "Bu alan boş kalamaz!" }
            else{ binding.emailInputLayout.error = null }

            if(password.isEmpty()){ binding.passwordInputLayout.error = "Bu alan boş kalamaz" }
            else { binding.passwordInputLayout.error = null }

            Toast.makeText(context,"Boş değerler var", Toast.LENGTH_LONG).show()
        }

    }


}