package com.android.module.phonebook.storage

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.android.module.phonebook.R
import com.bpcbt.android.pdk.presenter.interfaces.Action
import com.bpcbt.android.pdk.presenter.interfaces.ActivityResultRequester
import com.bpcbt.android.pdk.presenter.interfaces.RequestPermissionRequester
import com.bpcbt.inputs.dto.OperationSourceType
import com.bpcbt.inputs.dto.StorageSelector
import com.bpcbt.inputs.dto.input.StringInput
import com.bpcbt.inputs.validators.MaskValidator
import com.bpcbt.inputs.validators.watchers.AfterTextChangedTextWatcher
import com.bpcbt.inputs.validators.RegexValidator
import com.bpcbt.inputs.views.input.InputStereotypes
import com.bpcbt.inputs.views.input.string.StringInputPresenter
import com.bpcbt.inputs.views.input.string.phone.StoragePhoneInputView
import com.google.android.material.textfield.TextInputLayout
import ru.bpc.mobilebanksdk.dto.item.StorageEntry
import ru.bpc.mobilebanksdk.modulity.facilities.ComplexPaymentSourceItem
import ru.bpc.mobilebanksdk.modulity.facilities.MoneyStorageEntry
import ru.bpc.mobilebanksdk.modulity.facilities.PaymentSourceItem
import java.util.*


class StorageByPhoneNumberPaymentSourceItem(moneyStorageEntry: MoneyStorageEntry?) : ComplexPaymentSourceItem {

    private val KEY_PHONE_NUMBER = "phoneNumber"

    private val values = HashMap<String, String>(1)

    private val regexMask: String? = moneyStorageEntry?.phoneNumberPattern?.takeIf { it.isBlank().not() }

    private val jQueryMask: String? = moneyStorageEntry?.phoneNumberInternalMask


    override fun getSourceType() = OperationSourceType.PHONE

    override fun getCurrency(): String? = null

    override fun getSourceID(): String? = values[KEY_PHONE_NUMBER]

    override fun getParent(): PaymentSourceItem? = null

    override fun getView(context: Context, style: StorageSelector.Style?, activityResultRequester: ActivityResultRequester?, permissionRequester: RequestPermissionRequester?): View? {
        val view: View
        val maskPattern = if(regexMask != null) regexMask else jQueryMask
        if(style == StorageSelector.Style.COMPLEX) {
            val input = StringInput(KEY_PHONE_NUMBER, InputStereotypes.Strings.PHONE)
            input.mask = maskPattern
            val presenter = StringInputPresenter(input)
            view = StoragePhoneInputView(
                    context,
                    presenter,
                    activityResultRequester!!,
                    permissionRequester!!,
                    input.mask
            ).apply {
                setHint(context.getString(R.string.phone))
                jQueryMask?.let { setLength(it.length, it.length) }
            }

            presenter.attache(view, context)

            if(jQueryMask!=null && jQueryMask.isNotEmpty())
                presenter.accept(MaskValidator(jQueryMask.replace('x', '9')))
            if(regexMask != null && regexMask.isNotEmpty())
                presenter.accept(RegexValidator(regexMask))
            presenter.setValue("")
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.view_field_output, null)
            view.findViewById<TextView>(R.id.text_field_label).text = context.getString(R.string.phone)
            view.findViewById<TextView>(R.id.text_field_value).text = values[KEY_PHONE_NUMBER]
            view.findViewById<View>(R.id.text_field_description).visibility = View.GONE
        }
        return view
    }

    override fun toParameterValue(): String?  = String.format(
            "phoneNumber:%s",
            values[KEY_PHONE_NUMBER]
    )

    override fun toStorageEntry(): StorageEntry?  = null

    override fun isAllowed(moneyStorageEntry: MoneyStorageEntry): Boolean {
        return moneyStorageEntry.isByPhoneNumberAllowed
    }

    override fun setOnChangeAction(paymentSourceItemView: View?,
                                   onChangeAction: Action<PaymentSourceItem>?) {
        onChangeAction?.let { action ->
            paymentSourceItemView?.findViewById<TextInputLayout>(R.id.input_value_phone)?.apply {
                editText?.apply {
                    addTextChangedListener(PaymentSourceItemValueChangeListener(
                            values,
                            KEY_PHONE_NUMBER,
                            this@StorageByPhoneNumberPaymentSourceItem,
                            action
                    ))
                }
            }
        }
    }

    override fun isValid(paymentSourceItemView: View?): Boolean {
        val text = paymentSourceItemView?.findViewById<TextInputLayout>(R.id.input_value_phone)
                ?.editText
                ?.text
                .toString()
        if(text.length < jQueryMask?.length ?: 0) return false
        if(text.isEmpty()) return false
        return true
    }

    override fun getValues(): MutableMap<String, String> {
        return values
    }

    override fun saveValues(paymentSourceItemView: View?) {
        values[KEY_PHONE_NUMBER] = paymentSourceItemView?.findViewById<TextInputLayout>(
                R.id.input_value_phone
        )?.editText?.text.toString()
    }

    class PaymentSourceItemValueChangeListener(private val paramsMap: HashMap<String, String>,
                                               private val key: String,
                                               private val item: PaymentSourceItem,
                                               private val changeAction: Action<PaymentSourceItem>) : AfterTextChangedTextWatcher() {
        override fun afterTextChanged(p0: Editable?) {
            p0?.toString()?.let { paramsMap[key] = it }
            changeAction.action(item)
        }

    }
}