# دليل تثبيت وتشغيل تطبيق ClinicFlow

## متطلبات النظام

### للتطوير
- **Android Studio**: Flamingo أو أحدث
- **JDK**: 8 أو أحدث
- **Android SDK**: API Level 24 (Android 7.0) أو أحدث
- **Gradle**: 8.1.2 أو أحدث
- **Kotlin**: 1.9.10 أو أحدث

### للتشغيل
- **Android**: 7.0 (API Level 24) أو أحدث
- **RAM**: 2 GB أو أكثر
- **مساحة التخزين**: 100 MB أو أكثر
- **اتصال إنترنت**: مطلوب للمزامنة مع Firebase

## خطوات التثبيت

### 1. إعداد بيئة التطوير

#### تثبيت Android Studio
1. تنزيل Android Studio من [الموقع الرسمي](https://developer.android.com/studio)
2. تثبيت البرنامج واتباع معالج الإعداد
3. تنزيل Android SDK المطلوب (API Level 24+)

#### تثبيت JDK
```bash
# على Ubuntu/Debian
sudo apt update
sudo apt install openjdk-11-jdk

# على macOS (باستخدام Homebrew)
brew install openjdk@11

# على Windows
# تنزيل من موقع Oracle أو OpenJDK
```

### 2. إعداد مشروع Firebase

#### إنشاء مشروع Firebase
1. الذهاب إلى [Firebase Console](https://console.firebase.google.com/)
2. النقر على "إنشاء مشروع" أو استخدام المشروع الحالي `clinicflow-mqtu7`
3. اتباع خطوات إعداد المشروع

#### تفعيل الخدمات المطلوبة
1. **Authentication**:
   - الذهاب إلى Authentication > Sign-in method
   - تفعيل "Email/Password"
   - إنشاء مستخدم تجريبي للاختبار

2. **Firestore Database**:
   - الذهاب إلى Firestore Database
   - إنشاء قاعدة بيانات في وضع الاختبار
   - تعديل قواعد الأمان حسب الحاجة

3. **Storage**:
   - الذهاب إلى Storage
   - إعداد Firebase Storage
   - تعديل قواعد الأمان

#### إضافة تطبيق Android
1. في Firebase Console، النقر على "إضافة تطبيق" > Android
2. إدخال package name: `com.clinicflow.android`
3. تنزيل ملف `google-services.json`
4. وضع الملف في مجلد `app/` في المشروع

### 3. إعداد المشروع

#### استنساخ المشروع
```bash
# إذا كان المشروع في Git repository
git clone [repository-url]
cd ClinicFlowAndroid

# أو نسخ مجلد المشروع مباشرة
```

#### فتح المشروع في Android Studio
1. فتح Android Studio
2. اختيار "Open an existing Android Studio project"
3. تحديد مجلد `ClinicFlowAndroid`
4. انتظار تحميل المشروع ومزامنة Gradle

#### التحقق من إعدادات Gradle
```gradle
// في app/build.gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.clinicflow.android"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}
```

### 4. بناء وتشغيل التطبيق

#### بناء المشروع
```bash
# من سطر الأوامر
./gradlew build

# أو من Android Studio
Build > Make Project
```

#### تشغيل على محاكي
1. إنشاء AVD (Android Virtual Device):
   - Tools > AVD Manager
   - Create Virtual Device
   - اختيار جهاز (مثل Pixel 4)
   - اختيار نظام تشغيل (API 24+)

2. تشغيل التطبيق:
   - Run > Run 'app'
   - أو النقر على زر التشغيل الأخضر

#### تشغيل على جهاز حقيقي
1. تفعيل Developer Options على الجهاز:
   - Settings > About phone
   - النقر على Build number 7 مرات

2. تفعيل USB Debugging:
   - Settings > Developer options
   - تفعيل USB debugging

3. توصيل الجهاز بالكمبيوتر عبر USB
4. تشغيل التطبيق من Android Studio

### 5. إعداد بيانات الاختبار

#### إنشاء مستخدم تجريبي
```javascript
// في Firebase Console > Authentication > Users
Email: demo@clinicflow.com
Password: demo123456
```

#### إضافة بيانات تجريبية (اختياري)
```javascript
// في Firestore Console
// Collection: patients
{
  name: "أحمد محمد علي",
  phone: "0501234567",
  email: "ahmed@example.com",
  gender: "ذكر",
  createdAt: new Date(),
  isActive: true
}

// Collection: appointments
{
  patientId: "[patient-id]",
  patientName: "أحمد محمد علي",
  patientPhone: "0501234567",
  appointmentDate: new Date(),
  appointmentTime: "10:00",
  type: "CONSULTATION",
  status: "SCHEDULED",
  isActive: true
}
```

## استكشاف الأخطاء الشائعة

### خطأ في مزامنة Gradle
```bash
# حل المشكلة
./gradlew clean
./gradlew build --refresh-dependencies
```

### خطأ في Firebase
```
تأكد من:
- وجود ملف google-services.json في المكان الصحيح
- صحة package name في Firebase Console
- تفعيل الخدمات المطلوبة
```

### خطأ في البناء
```bash
# تنظيف المشروع
./gradlew clean

# إعادة بناء
./gradlew build
```

### مشاكل في المحاكي
```
- تأكد من توفر مساحة كافية على القرص الصلب
- زيادة RAM المخصص للمحاكي
- استخدام صورة نظام x86 بدلاً من ARM
```

## إعدادات الإنتاج

### تحسين الأداء
```gradle
// في app/build.gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

### إنشاء APK للتوزيع
```bash
# APK عادي
./gradlew assembleRelease

# Android App Bundle (مفضل للـ Play Store)
./gradlew bundleRelease
```

### توقيع التطبيق
1. إنشاء keystore:
```bash
keytool -genkey -v -keystore clinicflow-release-key.keystore -alias clinicflow -keyalg RSA -keysize 2048 -validity 10000
```

2. إضافة معلومات التوقيع في `app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file('clinicflow-release-key.keystore')
            storePassword 'your-store-password'
            keyAlias 'clinicflow'
            keyPassword 'your-key-password'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

## الدعم والمساعدة

### الموارد المفيدة
- [وثائق Android](https://developer.android.com/docs)
- [وثائق Firebase](https://firebase.google.com/docs)
- [وثائق Kotlin](https://kotlinlang.org/docs/)

### الحصول على المساعدة
- إنشاء Issue في GitHub repository
- مراجعة ملف README.md
- التواصل مع فريق التطوير

### تحديث التطبيق
```bash
# سحب آخر التحديثات
git pull origin main

# مزامنة المشروع
./gradlew clean build
```

---

© 2024 ClinicFlow. جميع الحقوق محفوظة.

