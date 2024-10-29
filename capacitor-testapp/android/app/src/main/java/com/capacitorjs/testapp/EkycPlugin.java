package com.capacitorjs.testapp;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.vnptit.idg.sdk.activity.VnptIdentityActivity;
import com.vnptit.idg.sdk.utils.KeyIntentConstants;
import com.vnptit.idg.sdk.utils.KeyResultConstants;
import com.vnptit.idg.sdk.utils.SDKEnum;

@CapacitorPlugin(name = "EkycPlugin")
public class EkycPlugin extends Plugin {

   // Phương thức thực hiện eKYC luồng đầy đủ bao gồm: Chụp ảnh giấy tờ và chụp ảnh
   // chân dung
   // Bước 1 - chụp ảnh giấy tờ
   // Bước 2 - chụp ảnh chân dung xa gần
   // Bước 3 - hiển thị kết quả
   @PluginMethod()
   public void startEkycFull(final PluginCall call) {
      final Activity ctx = getActivity();
      if (ctx == null) {
         return;
      }

      final Intent intent = new Intent(ctx, VnptIdentityActivity.class);

      // Nhập thông tin bộ mã truy cập. Lấy tại mục Quản lý Token
      // https://ekyc.vnpt.vn/admin-dashboard/console/project-manager
      intent.putExtra(KeyIntentConstants.ACCESS_TOKEN, call.getString("accessToken", ""));
      intent.putExtra(KeyIntentConstants.TOKEN_ID, call.getString("tokenId", ""));
      intent.putExtra(KeyIntentConstants.TOKEN_KEY, call.getString("tokenKey", ""));

      // Giá trị này dùng để đảm bảo mỗi yêu cầu (request) từ phía khách hàng sẽ
      // không bị thay đổi.
      intent.putExtra(KeyIntentConstants.CHALLENGE_CODE, "INNOVATIONCENTER");

      // Ngôn ngữ sử dụng trong SDK
      // - VIETNAMESE: Tiếng Việt
      // - ENGLISH: Tiếng Anh
      intent.putExtra(KeyIntentConstants.LANGUAGE_SDK, SDKEnum.LanguageEnum.VIETNAMESE.getValue());

      // Bật/Tắt Hiển thị màn hình hướng dẫn
      intent.putExtra(KeyIntentConstants.IS_SHOW_TUTORIAL, true);

      // Bật chức năng hiển thị nút bấm "Bỏ qua hướng dẫn" tại các màn hình hướng dẫn
      // bằng video
      intent.putExtra(KeyIntentConstants.IS_ENABLE_GOT_IT, true);

      // Giá trị này xác định kiểu giấy tờ để sử dụng:
      // - IDENTITY_CARD: Chứng minh thư nhân dân, Căn cước công dân
      // - IDCardChipBased: Căn cước công dân gắn Chip
      // - Passport: Hộ chiếu
      // - DriverLicense: Bằng lái xe
      // - MilitaryIdCard: Chứng minh thư quân đội
      intent.putExtra(KeyIntentConstants.DOCUMENT_TYPE, SDKEnum.DocumentTypeEnum.IDENTITY_CARD.getValue());

      // Bật/Tắt chức năng So sánh ảnh trong thẻ và ảnh chân dung
      intent.putExtra(KeyIntentConstants.IS_COMPARE_FLOW, true);

      // Bật/Tắt chức năng kiểm tra ảnh giấy tờ chụp trực tiếp (liveness card)
      intent.putExtra(KeyIntentConstants.IS_CHECK_LIVENESS_CARD, true);

      // Lựa chọn chức năng kiểm tra ảnh chân dung chụp trực tiếp (liveness face)
      // - NoneCheckFace: Không thực hiện kiểm tra ảnh chân dung chụp trực tiếp hay
      // không
      // - iBETA: Kiểm tra ảnh chân dung chụp trực tiếp hay không iBeta (phiên bản
      // hiện tại)
      // - Standard: Kiểm tra ảnh chân dung chụp trực tiếp hay không Standard (phiên
      // bản mới)
      intent.putExtra(KeyIntentConstants.CHECK_LIVENESS_FACE, SDKEnum.ModeCheckLiveNessFace.iBETA.getValue());

      // Bật/Tắt chức năng kiểm tra che mặt
      intent.putExtra(KeyIntentConstants.IS_CHECK_MASKED_FACE, true);

      // Lựa chọn chế độ kiểm tra ảnh giấy tờ ngay từ SDK
      // - None: Không thực hiện kiểm tra ảnh khi chụp ảnh giấy tờ
      // - Basic: Kiểm tra sau khi chụp ảnh
      // - MediumFlip: Kiểm tra ảnh hợp lệ trước khi chụp (lật giấy tờ thành công →
      // hiển thị nút chụp)
      // - Advance: Kiểm tra ảnh hợp lệ trước khi chụp (hiển thị nút chụp)
      intent.putExtra(KeyIntentConstants.TYPE_VALIDATE_DOCUMENT, SDKEnum.TypeValidateDocument.Basic.getValue());

      // Giá trị này xác định việc có xác thực số ID với mã tỉnh thành, quận huyện,
      // xã phường tương ứng hay không.
      intent.putExtra(KeyIntentConstants.IS_VALIDATE_POSTCODE, true);

      intent.putExtra(KeyIntentConstants.VERSION_SDK, SDKEnum.VersionSDKEnum.ADVANCED.getValue());

      startActivityForResult(call, intent, "ekycFullResult");
   }

   @ActivityCallback
   private void ekycFullResult(final PluginCall call, final ActivityResult result) {
      if (call == null)
         return;
      final Intent data = result.getData();
      if (data == null)
         return;

      /*
       * Dữ liệu bóc tách thông tin OCR
       * {@link KeyResultConstants#INFO_RESULT}
       */
      final String dataInfoResult = data.getStringExtra(KeyResultConstants.INFO_RESULT);

      /*
       * Dữ liệu bóc tách thông tin Liveness card mặt trớc
       * {@link KeyResultConstants#LIVENESS_CARD_FRONT_RESULT}
       */
      final String dataLivenessCardFrontResult = data.getStringExtra(KeyResultConstants.LIVENESS_CARD_FRONT_RESULT);

      /*
       * Dữ liệu bóc tách thông tin liveness card mặt sau
       * {@link KeyResultConstants#LIVENESS_CARD_REAR_RESULT}
       */
      final String dataLivenessCardRearResult = data.getStringExtra(KeyResultConstants.LIVENESS_CARD_REAR_RESULT);

      /*
       * Dữ liệu bóc tách thông tin compare face
       * {@link KeyResultConstants#COMPARE_RESULT}
       */
      final String dataCompareResult = data.getStringExtra(KeyResultConstants.COMPARE_RESULT);

      /*
       * Dữ liệu bóc tách thông tin liveness face
       * {@link KeyResultConstants#LIVENESS_FACE_RESULT}
       */
      final String dataLivenessFaceResult = data.getStringExtra(KeyResultConstants.LIVENESS_FACE_RESULT);

      /*
       * Dữ liệu bóc tách thông tin mask face
       * {@link KeyResultConstants#MASKED_FACE_RESULT}
       */
      final String dataMaskedFaceResult = data.getStringExtra(KeyResultConstants.MASKED_FACE_RESULT);

      final JSObject obj = new JSObject();
      obj.put("ocrResult", dataInfoResult);
      obj.put("livenessCardFrontResult", dataLivenessCardFrontResult);
      obj.put("livenessCardBackResult", dataLivenessCardRearResult);
      obj.put("livenessFaceResult", dataLivenessFaceResult);
      obj.put("maskedFaceResult", dataMaskedFaceResult);
      obj.put("compareFaceResult", dataCompareResult);

      call.resolve(obj);
   }
}
