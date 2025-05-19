package com.example.growpath.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.growpath.GrowPathApp
import com.example.growpath.screen.DashboardViewModel
import com.example.growpath.screen.ExploreViewModel
import com.example.growpath.screen.MilestoneViewModel
import com.example.growpath.screen.ProfileViewModel
import com.example.growpath.screen.RoadmapViewModel

/**
 * Factory untuk membuat ViewModel dengan dependensi yang diperlukan
 */
class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RoadmapViewModel::class.java) -> {
                RoadmapViewModel(GrowPathApp.instance.roadmapRepository) as T
            }
            modelClass.isAssignableFrom(MilestoneViewModel::class.java) -> {
                MilestoneViewModel(GrowPathApp.instance.roadmapRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(GrowPathApp.instance.userRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(GrowPathApp.instance.roadmapRepository) as T
            }
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> {
                ExploreViewModel(GrowPathApp.instance.roadmapRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
