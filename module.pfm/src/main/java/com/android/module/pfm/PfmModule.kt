package com.android.module.pfm

import com.android.module.pfm.ui.main.PfmMainActivity
import ru.bpc.mobilebanksdk.modulity.facilities.MainMenuItem
import ru.bpc.mobilebanksdk.modulity.module.MainMenuItemUser


class PfmModule : MainMenuItemUser {

    override fun getMenuVisibilityKey() = "module.pfm.menu"

    override fun getMainMenuPositionOrder() = 600

    override fun getMainMenuItem(): MainMenuItem<*> = MainMenuItem(
            R.string.title_menu_pfm,
            R.drawable.ic_sidebar_pfm,
            PfmMainActivity::class.java
    )
}