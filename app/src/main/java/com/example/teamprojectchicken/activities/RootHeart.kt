package com.example.teamprojectchicken.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.teamprojectchicken.R

class RootHeart : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_root_heart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentById(R.id.root2_frag)==null){
            childFragmentManager.beginTransaction().apply {
                replace(R.id.root2_frag, HeartFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        childFragmentManager.beginTransaction().apply {
            replace(R.id.root2_frag, HeartFragment())
            addToBackStack(null)
            commit()
        }
    }
}