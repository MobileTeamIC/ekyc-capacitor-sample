import { registerPlugin } from '@capacitor/core';

export type EkycResult = {
  ocrResult?: string,
  livenessCardFrontResult?: string,
  livenessCardBackResult?: string,
  livenessFaceResult?: string,
  maskedFaceResult?: string,
  compareFaceResult?: string
};

export interface EkycPlugin {
  startEkycFull(options: { accessToken: string, tokenId: string, tokenKey: string }): Promise<EkycResult>;
}

const Ekyc = registerPlugin<EkycPlugin>('EkycPlugin');

export default Ekyc;