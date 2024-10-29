# ekyc-capacitor-sample

Dự án mẫu thực hiện việc tích hợp SDK VNPT eKYC cho ứng dụng di động (Capacitor)

### cấu hình:
```
npm install -g ionic
npm install -g @ionic/cli
```

### 
- vào project:
```
npm install
```
- run app: 
```
npm run build
npx cap sync
ionic capacitor run ios --livereload --external (chạy ios platform)
```
- cầu nối: 
  + đặt tên là 'EkycPlugin'
  + hàm mở ekyc full của cầu nối đặt tên là 'startEkycFull'

- đầu vào mở SDK với file cầu nối:
  + accessToken, tokenId, tokenKey (truyền từ ionic vào, file cầu nối sẽ nhận 3 key này để truyền vào SDK eKYC)

- đầu ra từ file cầu nối cho ionic:
  + ocrResult, livenessCardFrontResult, livenessCardBackResult, livenessFaceResult, maskedFaceResult, compareFaceResult (các key từ file cầu nối trả về sau khi hoàn thành luồng full eKYC trả ra ngoài cho ionic)
