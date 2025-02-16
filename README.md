<!-- TOC -->
* [🎮 미니게임 천국 (Mini Game Heaven)](#-미니게임-천국-mini-game-heaven)
  * [📌 소개](#-소개)
    * [주요 게임](#주요-게임)
  * [🚀 주요 기능](#-주요-기능)
    * [✋ 가위바위보 게임](#-가위바위보-게임)
      * [RspActivity(Rock-Scissor-Paper game Activity)](#rspactivityrock-scissor-paper-game-activity)
    * [🔥 메모리 게임](#-메모리-게임)
    * [🐍 스네이크 게임](#-스네이크-게임)
  * [🖼️ 이미지 처리 및 랭킹](#-이미지-처리-및-랭킹)
<!-- TOC -->

# 🎮 미니게임 천국 (Mini Game Heaven)

## 📌 소개

**미니게임 천국**은 Android 하드웨어 기능을 활용한 간단하지만 재미있는 게임 모음입니다.
**GPIO, 카메라, OpenCV** 등을 활용하여 인터랙티브한 게임 경험을 제공합니다.

### 주요 게임

- **가위바위보 게임**: OpenCV를 이용한 손 동작 감지 기능 구현
- **메모리 게임**: LED와 JNI를 이용한 기억력 게임
- **스네이크 게임**: GPIO 버튼 입력을 통한 스네이크 게임

---

## 🚀 주요 기능

### ✋ 가위바위보 게임

- **OpenCV Color Blob Detection**을 활용한 손 인식
- **HSV 색 공간**을 활용한 피부 색상 감지
- **OpenCV Gaussian Blur & Edge Detection**을 통한 영상 전처리
- **SeekBar**를 이용한 감도 조정 UI 제공

#### RspActivity(Rock-Scissor-Paper game Activity)

![img.png](Rock-Scissor-Paper-game-Activity.png)

1. Override `onTouch()` Method
2. Get the position(X, Y) of the touched screen
3. Calculate average color of touched region
4. Converts an image from one color space to another.
5. Convert The input Scalar(HSV OR RGB) to output Matrix
6. Resize the image src down to or up to the specified size

[RspActivity.java](app/src/main/java/com/example/minigame/RspActivity.java) – Connect OpenCV &
onCreate

* Initialize the CameraSurfaceView(extends JavaCameraView)
* JavaCameraView is the Bridge View between OpenCV and Java Camera.
* Progress set the Threshold that set how sensitive you want to differentiate.
* And you can set the progress with seekbar.
  ![img.png](rock-scissor-papger-game-hand-detection.png)

### 🔥 메모리 게임

- **JNI Driver**를 사용하여 LED 제어
- **CountDownTimer**를 활용한 게임 진행
- **싱글톤 패턴 (`GameInfo.java`)**을 이용한 게임 상태 관리
- 오답 입력 감지 후 **`GameOverActivity.java`**로 이동
  ![img_1.png](memory-test-game-image.png)
-

### 🐍 스네이크 게임

- **Interrupt(GPIO)와 JNI**를 활용한 입력 처리
- **Handler & Looper**를 사용하여 실시간 입력 감지
- **Canvas & SurfaceView**를 이용한 동적 애니메이션

![img_1.png](snake-game-activity-structure.png)

1. Draw on Canvas  
   a. Lock the Canvas  
   b. Draw the components of the snake using Canvas method(ex. drawCircle, drawColor)  
   c. Unlock Canvas  
2. Post the Canvas(Surface) to Surface Holder

![img_1.png](snake-game-play-image.png)

---

## 🖼️ 이미지 처리 및 랭킹

- 사용자가 게임 내에서 **카메라를 이용해 이미지를 캡처** 가능
- OpenCL을 활용한 **이미지 필터링 (Gaussian Blur, Edge Detection)**
- **RecyclerView**를 이용하여 과거 플레이어 랭킹 표시
- ![img.png](feature-record-rank.png)

---

