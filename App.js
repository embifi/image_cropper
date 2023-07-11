import React, { useEffect, useState } from "react";
import { View, Text, StyleSheet, Image, Button } from "react-native";
import { startNativeActivity } from "./BridgeUtils";
import NativeModuleListener from "./NativeListenerUtils";

var uri = ''

const handleNavigatetoNativeSide = () => {
  startNativeActivity();
};


const App = (props) => {
  // const [nativeMessage, setNativeMessage] = useState();
  const [croppedImageBase64, setCroppedImageBase64] = useState();

  const handleNativeData = (info) => {

    if (info) {
      // console.log("info: ", info);
      const { croppedImageBase64} = info;
      setCroppedImageBase64(croppedImageBase64); // Set the base64 image
      uri = croppedImageBase64; // Set the base64 image
    }
  };

  useEffect(() => {
    const customEmitter = NativeModuleListener.addActionListener("dataCallback", handleNativeData);

    
    return () => {
      if (customEmitter) {
        customEmitter.remove();
      }
    };
  }, []);

  return (
    <View style={styles.container}>

      <Image
        style={styles.image}
        source={{uri: `data:image/png;base64,${uri}`}}
      />

      <Button
        title="Upload Document"
        onPress={() => handleNavigatetoNativeSide()}
      />
    </View>
  );
};

export default App;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  image: {
    width: 200,
    height: 200,
    resizeMode: "contain",
  },
});
