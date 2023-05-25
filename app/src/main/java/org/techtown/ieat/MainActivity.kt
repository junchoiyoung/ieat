package org.techtown.ieat

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.techtown.ieat.databinding.ActivityMainBinding
import java.io.File

private const val TAG_HOME = "home_fragment"
private const val TAG_RECIPE = "recipe_fragment"
private const val TAG_MYRECIPE = "myrecipe_fragment"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.recipeFragment -> setFragment(TAG_RECIPE, RecipeFragment())
                R.id.mainFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.myrecipeFragment-> setFragment(TAG_MYRECIPE, MyrecipeFragment())
            }
            true
        }


    }


    private fun setFragment(tag:String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val recipe = manager.findFragmentByTag(TAG_RECIPE)
        val home = manager.findFragmentByTag(TAG_HOME)
        val myRecipe = manager.findFragmentByTag(TAG_MYRECIPE)

        if (recipe != null){
            fragTransaction.hide(recipe)
        }

        if (home != null){
            fragTransaction.hide(home)
        }

        if (myRecipe != null) {
            fragTransaction.hide(myRecipe)
        }

        if (tag == TAG_RECIPE) {
            if (recipe!=null){
                fragTransaction.show(recipe)
            }
        }
        else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_MYRECIPE){
            if (myRecipe != null){
                fragTransaction.show(myRecipe)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}
