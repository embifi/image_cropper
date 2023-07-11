import { NativeModules } from "react-native";
const {CustomModule} = NativeModules;

export const startNativeActivity = () => {
    const message = "Message from React Side";  
    CustomModule.startActivity(message);
}