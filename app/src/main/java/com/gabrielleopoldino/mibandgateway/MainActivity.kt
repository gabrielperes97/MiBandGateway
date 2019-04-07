package com.gabrielleopoldino.mibandgateway

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Switch


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner1 = findViewById<Spinner>(R.id.spinner1)
        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        val spinner3 = findViewById<Spinner>(R.id.spinner3)
        val spinners = listOf<Spinner>(spinner1, spinner2, spinner3)

        val service : ButtonService = ButtonService(this)

        var list =  Actions.actionList.keys.toList().toMutableList()
        list.add(0, "")

        spinners.forEach { spinner ->
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list)
            spinner.adapter = adapter as SpinnerAdapter?
        }

        val switch = findViewById<Switch>(R.id.btswitch)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
            {
                val act1 : (Context) -> Unit
                val act2 : (Context) -> Unit
                val act3 : (Context) -> Unit

                var item : String = spinner1.selectedItem.toString()
                var func = Actions.actionList[item]
                if (func == null)
                    act1 = {}
                else
                    act1 = func

                item = spinner2.selectedItem.toString()
                func = Actions.actionList[item]
                if (func == null)
                    act2 = {}
                else
                    act2 = func

                item = spinner3.selectedItem.toString()
                func = Actions.actionList[item]
                if (func == null)
                    act3 = {}
                else
                    act3 = func


                val listener = object : ButtonListener{
                    override fun oneClick(){
                        act1(this@MainActivity)
                    }

                    override fun twoClick(){
                        act2(this@MainActivity)
                    }

                    override fun threeClick(){
                        act3(this@MainActivity)
                    }
                }
                service.startAtFirstDevice(listener)
            }
            else
            {
                service.clean()
            }
        }

        service.addOnDisconnectListener {
            switch.isChecked = false
        }










    }


}
