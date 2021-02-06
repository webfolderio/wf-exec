cmake_minimum_required(VERSION 3.8.0)

project(wf-exec)

set(CMAKE_CXX_STANDARD 14)

add_definitions(-D_WIN32_WINNT=0x0601)

find_package(JNI REQUIRED)

INCLUDE_DIRECTORIES(${JAVA_INCLUDE_PATH})
INCLUDE_DIRECTORIES(${JAVA_INCLUDE_PATH2})

add_library(wf-exec SHARED
  file.cc
  file-jni.cc
  jni-util.cc
  process.cc
  process-jni.cc
  util.cc
)

set_property(DIRECTORY
  ${CMAKE_CURRENT_SOURCE_DIR}
  PROPERTY
  VS_STARTUP_PROJECT
  wf-exec)