import {
  IonButton,
  IonContent,
  IonHeader,
  IonPage,
  IonTitle,
  IonToolbar,
} from '@ionic/react';
import React from 'react';
import Ekyc, { EkycResult } from '../bridge/ekyc_bridge';

const Home: React.FC = () => {
  const openEkycFull = async (): Promise<void> => {
    const ekycResult: EkycResult = await Ekyc.startEkycFull({
      accessToken: '<access_token> (including bearer)',
      tokenId: '<token_id>',
      tokenKey: '<token_key>',
    });
    console.log(ekycResult);
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Tích hợp SDK VNPT eKYC</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent className="ion-padding">
        <IonButton expand="full" onClick={openEkycFull}>
          Thực hiện luồng đầy đủ
        </IonButton>
      </IonContent>
    </IonPage>
  );
};

export default Home;
