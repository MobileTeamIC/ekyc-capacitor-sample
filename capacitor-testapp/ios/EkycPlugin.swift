//
//  EkycPlugin.swift
//  App
//
//  Created by Longcon99 on 22/10/24.
//

import Capacitor
import ICSdkEKYC

@objc(EkycPlugin)
public class EkycPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "EkycPlugin"
    public let jsName = "EkycPlugin"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "startEkycFull", returnType: CAPPluginReturnPromise)
    ]

    private var call: CAPPluginCall?
    
    // Phương thức thực hiện eKYC luồng đầy đủ bao gồm: Chụp ảnh giấy tờ và chụp ảnh chân dung
    // Bước 1 - chụp ảnh giấy tờ
    // Bước 2 - chụp ảnh chân dung xa gần
    // Bước 3 - hiển thị kết quả
    @objc func startEkycFull(_ call: CAPPluginCall) {
        ICEKYCSavedData.shared().authorization = call.getString("accessToken") ?? ""
        ICEKYCSavedData.shared().tokenId = call.getString("tokenId") ?? ""
        ICEKYCSavedData.shared().tokenKey = call.getString("tokenKey") ?? ""
        self.call = call
        
        let objCamera = ICEkycCameraRouter.createModule() as! ICEkycCameraViewController
        objCamera.cameraDelegate = self

        // Giá trị này xác định phiên bản khi sử dụng Máy ảnh tại bước chụp ảnh chân dung luồng full. Mặc định là Normal ✓
        // - Normal: chụp ảnh chân dung 1 hướng
        // - ProOval: chụp ảnh chân dung xa gần
        objCamera.versionSdk = ProOval
        
        // Giá trị xác định luồng thực hiện eKYC
        // - full: thực hiện eKYC đầy đủ các bước: chụp mặt trước, chụp mặt sau và chụp ảnh chân dung
        // - ocrFront: thực hiện OCR giấy tờ một bước: chụp mặt trước
        // - ocrBack: thực hiện OCR giấy tờ một bước: chụp mặt trước
        // - ocr: thực hiện OCR giấy tờ đầy đủ các bước: chụp mặt trước, chụp mặt sau
        // - face: thực hiện so sánh khuôn mặt với mã ảnh chân dung được truyền từ bên ngoài
        objCamera.flowType = full
        
        // Giá trị này xác định kiểu giấy tờ để sử dụng:
        // - IdentityCard: Chứng minh thư nhân dân, Căn cước công dân
        // - IDCardChipBased: Căn cước công dân gắn Chip
        // - Passport: Hộ chiếu
        // - DriverLicense: Bằng lái xe
        // - MilitaryIdCard: Chứng minh thư quân đội
        objCamera.documentType = IdentityCard
        
        // Giá trị này dùng để đảm bảo mỗi yêu cầu (request) từ phía khách hàng sẽ không bị thay đổi.
        objCamera.challengeCode = "INNOVATIONCENTER"
        
        // Bật/Tắt Hiển thị màn hình hướng dẫn
        objCamera.isShowTutorial = true
        
        // Bật/Tắt chức năng So sánh ảnh trong thẻ và ảnh chân dung
        objCamera.isEnableCompare = true
        
        // Bật/Tắt chức năng kiểm tra che mặt
        objCamera.isCheckMaskedFace = true
        
        // Lựa chọn chức năng kiểm tra ảnh chân dung chụp trực tiếp (liveness face)
        // - NoneCheckFace: Không thực hiện kiểm tra ảnh chân dung chụp trực tiếp hay không
        // - IBeta: Kiểm tra ảnh chân dung chụp trực tiếp hay không iBeta (phiên bản hiện tại)
        // - Standard: Kiểm tra ảnh chân dung chụp trực tiếp hay không Standard (phiên bản mới)
        objCamera.checkLivenessFace = IBeta
        
        // Bật/Tắt chức năng kiểm tra ảnh giấy tờ chụp trực tiếp (liveness card)
        objCamera.isCheckLivenessCard = true
        
        // Lựa chọn chế độ kiểm tra ảnh giấy tờ ngay từ SDK
        // - None: Không thực hiện kiểm tra ảnh khi chụp ảnh giấy tờ
        // - Basic: Kiểm tra sau khi chụp ảnh
        // - MediumFlip: Kiểm tra ảnh hợp lệ trước khi chụp (lật giấy tờ thành công → hiển thị nút chụp)
        // - Advance: Kiểm tra ảnh hợp lệ trước khi chụp (hiển thị nút chụp)
        objCamera.validateDocumentType = Basic
        
        // Bật chức năng hiển thị nút bấm "Bỏ qua hướng dẫn" tại các màn hình hướng dẫn bằng video
        objCamera.isEnableGotIt = true
        
        // Ngôn ngữ sử dụng trong SDK
        objCamera.languageSdk = "icekyc_vi"
        
        // Bật/Tắt Hiển thị ảnh thương hiệu
        objCamera.isShowTrademark = true
        
        DispatchQueue.main.async {
            objCamera.modalPresentationStyle = .fullScreen
            objCamera.modalTransitionStyle = .coverVertical
            self.bridge?.viewController?.present(objCamera, animated: true)
        }
        
    }
    
}

extension EkycPlugin: ICEkycCameraDelegate {
    
    public func icEkycGetResult() {
        // Thông tin bóc tách OCR
        let ocrResult = ICEKYCSavedData.shared().ocrResult
        
        // Kết quả kiểm tra giấy tờ chụp trực tiếp (Liveness Card) mặt trước
        let livenessCardFrontResult = ICEKYCSavedData.shared().livenessCardFrontResult
        
        // Kết quả kiểm tra giấy tờ chụp trực tiếp (Liveness Card) mặt sau
        let livenessCardBackResult = ICEKYCSavedData.shared().livenessCardBackResult
        
        // Dữ liệu thực hiện SO SÁNH khuôn mặt (lấy từ mặt trước ảnh giấy tờ hoặc ảnh thẻ)
        let compareFaceResult = ICEKYCSavedData.shared().compareFaceResult
        
        // Dữ liệu kiểm tra ảnh CHÂN DUNG chụp trực tiếp hay không
        let livenessFaceResult = ICEKYCSavedData.shared().livenessFaceResult
        
        // Dữ liệu kiểm tra ảnh CHÂN DUNG có bị che mặt hay không
        let maskedFaceResult = ICEKYCSavedData.shared().maskedFaceResult
        
        call?.resolve(["ocrResult": ocrResult,
                       "livenessCardFrontResult": livenessCardFrontResult,
                       "livenessCardBackResult": livenessCardBackResult,
                       "livenessFaceResult": livenessFaceResult,
                       "maskedFaceResult": maskedFaceResult,
                       "compareFaceResult": compareFaceResult])
    }
    
    public func icEkycCameraClosed(with type: ScreenType) {
        print("icEkycCameraClosed = \(type)")
    }
    
}
