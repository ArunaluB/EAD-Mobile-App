package edu.sliit.operator.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.sliit.operator.fragments.BookingsFragment
import edu.sliit.operator.fragments.DashboardFragment
import edu.sliit.operator.fragments.ProfileFragment
import edu.sliit.operator.fragments.QRScannerFragment
import edu.sliit.operator.utils.AppColors

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var bottomNavigation: LinearLayout
    private val navItems = mutableListOf<NavigationItem>()
    private var currentSelectedIndex = 0

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d(TAG, "MainActivity onCreate started")
            
            // Get username from intent
            val username = intent.getStringExtra("username")
            Log.d(TAG, "Received username: $username")
            
            // Create and set the main view
            val mainView = createMainView()
            setContentView(mainView)
            Log.d(TAG, "Main view created successfully")
            
            // Load initial fragment
            loadFragment(DashboardFragment())
            Log.d(TAG, "Dashboard fragment loaded successfully")

        } catch (e: Exception) {
            Log.e(TAG, "ERROR in MainActivity onCreate: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
            Toast.makeText(this, "Error loading dashboard: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createMainView(): LinearLayout {
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL
        mainLayout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mainLayout.setBackgroundColor(Color.parseColor(AppColors.BLACK_PRIMARY))

        // Create fragment container
        fragmentContainer = FrameLayout(this)
        fragmentContainer.id = android.view.View.generateViewId()
        val containerParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        )
        fragmentContainer.layoutParams = containerParams
        mainLayout.addView(fragmentContainer)

        // Create bottom navigation
        bottomNavigation = createBottomNavigation()
        mainLayout.addView(bottomNavigation)

        return mainLayout
    }

    private fun createBottomNavigation(): LinearLayout {
        val navLayout = LinearLayout(this)
        navLayout.orientation = LinearLayout.HORIZONTAL
        navLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        navLayout.setBackgroundColor(Color.parseColor(AppColors.BLACK_OVERLAY))
        navLayout.elevation = 16f
        navLayout.setPadding(0, 20, 0, 20)

        // Add navigation items
        addNavigationItem(navLayout, "🏠", "Dashboard", 0)
        addNavigationItem(navLayout, "📋", "Bookings", 1)
        addNavigationItem(navLayout, "📷", "QR Scan", 2)
        addNavigationItem(navLayout, "👤", "Profile", 3)

        return navLayout
    }

    private fun addNavigationItem(parent: LinearLayout, icon: String, label: String, index: Int) {
        val navItem = createNavigationItemView(icon, label, index)
        navItems.add(navItem)
        parent.addView(navItem.container)

        if (index == 0) {
            selectNavigationItem(0)
        }
    }

    private fun createNavigationItemView(icon: String, label: String, index: Int): NavigationItem {
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        container.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        container.setPadding(16, 16, 16, 16)
        container.isClickable = true
        container.isFocusable = true
        container.setOnClickListener {
            selectNavigationItem(index)
        }

        val iconView = TextView(this)
        iconView.text = icon
        iconView.textSize = 24f
        iconView.gravity = Gravity.CENTER
        iconView.setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
        container.addView(iconView)

        val labelView = TextView(this)
        labelView.text = label
        labelView.textSize = 12f
        labelView.gravity = Gravity.CENTER
        labelView.setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
        val labelParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        labelParams.setMargins(0, 8, 0, 0)
        labelView.layoutParams = labelParams
        container.addView(labelView)

        return NavigationItem(container, iconView, labelView, index)
    }

    private fun selectNavigationItem(index: Int) {
        if (currentSelectedIndex == index) return

        navItems[currentSelectedIndex].apply {
            iconView.setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
            labelView.setTextColor(Color.parseColor(AppColors.GRAY_MEDIUM))
        }

        navItems[index].apply {
            iconView.setTextColor(Color.parseColor(AppColors.GREEN_NEON))
            labelView.setTextColor(Color.parseColor(AppColors.GREEN_NEON))
        }

        currentSelectedIndex = index

        val fragment = when (index) {
            0 -> DashboardFragment()
            1 -> BookingsFragment()
            2 -> QRScannerFragment()
            3 -> ProfileFragment()
            else -> DashboardFragment()
        }

        loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment) {
        try {
            Log.d(TAG, "Loading fragment: ${fragment.javaClass.simpleName}")
            supportFragmentManager.beginTransaction()
                .replace(fragmentContainer.id, fragment)
                .commitAllowingStateLoss()
            Log.d(TAG, "Fragment loaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading fragment: ${e.message}")
            Log.e(TAG, "Stack trace: ", e)
        }
    }

    data class NavigationItem(
        val container: LinearLayout,
        val iconView: TextView,
        val labelView: TextView,
        val index: Int
    )
}