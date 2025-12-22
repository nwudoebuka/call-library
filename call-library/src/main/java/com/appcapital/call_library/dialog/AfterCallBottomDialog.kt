package com.appcapital.call_library.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import com.appcapital.call_library.R
import com.appcapital.call_library.databinding.DialogAfterCallBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AfterCallBottomDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogAfterCallBinding


    private var entityId: String? = null
    private var requestId: String? = null
    private var msisdn: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // setStyle(STYLE_NORMAL, R.style.cryptoAuthBottomDialogTheme)

        // getExtras()


    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)

        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.setCanceledOnTouchOutside(false)

            parentLayout?.let { parent ->
                val behaviour = BottomSheetBehavior.from(parent)
                setupFullHeight(parent)
                behaviour.peekHeight = 50
                behaviour.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                behaviour.isDraggable = false
                behaviour.isHideable = false
            }
        }
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    /**
     * makes bottom sheet to fill screen, this is to fit to the design
     */
    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAfterCallBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        //initOnClickListener()

        return binding.root

    }
}

//
//
//    private fun initOnClickListener(){
//        binding.submitButton.setOnClickListener {
//            dismiss()
//            moveToCreatePinScreen()
//        }
//    }
//
//    private fun moveToCreatePinScreen() {
//        val intent = Intent(requireActivity(), CreatePInActivity::class.java)
//        intent.putExtra(AccountTypeSelectorBottomDialog.ACCOUNT_SELECTOR_TYPE, accountType)
//        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//        intent.putExtra(Constants.PERSONAL_INFO_TYPE, PersonalInfoType.INDIVIDUAL)
//        startActivity(intent)
//    }
//
//
//
//    companion object {
//        fun newInstance(): IndividualAccountSuccessDialog {
//            return IndividualAccountSuccessDialog()
//        }
//    }
