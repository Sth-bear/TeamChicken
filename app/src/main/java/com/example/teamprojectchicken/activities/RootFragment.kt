package com.example.teamprojectchicken.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import com.example.teamprojectchicken.R
import com.example.teamprojectchicken.data.Contact

class RootFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onStart() {
        super.onStart()
        childFragmentManager.beginTransaction().apply {
            replace(R.id.root_frag, ContactListFragment())
            commit()
        }
    }

    override fun onPause() {
        super.onPause()
        childFragmentManager.beginTransaction().apply {
            replace(R.id.root_frag, ContactListFragment())
            commit()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_root, container, false)
    }
}