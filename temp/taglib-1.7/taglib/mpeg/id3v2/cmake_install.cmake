# Install script for directory: C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "C:/Program Files (x86)/taglib")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

if(NOT CMAKE_INSTALL_COMPONENT OR "${CMAKE_INSTALL_COMPONENT}" STREQUAL "Unspecified")
  list(APPEND CMAKE_ABSOLUTE_DESTINATION_FILES
   "C:/Program Files (x86)/taglib/include/taglib/id3v2extendedheader.h;C:/Program Files (x86)/taglib/include/taglib/id3v2frame.h;C:/Program Files (x86)/taglib/include/taglib/id3v2header.h;C:/Program Files (x86)/taglib/include/taglib/id3v2synchdata.h;C:/Program Files (x86)/taglib/include/taglib/id3v2footer.h;C:/Program Files (x86)/taglib/include/taglib/id3v2framefactory.h;C:/Program Files (x86)/taglib/include/taglib/id3v2tag.h")
  if(CMAKE_WARN_ON_ABSOLUTE_INSTALL_DESTINATION)
    message(WARNING "ABSOLUTE path INSTALL DESTINATION : ${CMAKE_ABSOLUTE_DESTINATION_FILES}")
  endif()
  if(CMAKE_ERROR_ON_ABSOLUTE_INSTALL_DESTINATION)
    message(FATAL_ERROR "ABSOLUTE path INSTALL DESTINATION forbidden (by caller): ${CMAKE_ABSOLUTE_DESTINATION_FILES}")
  endif()
file(INSTALL DESTINATION "C:/Program Files (x86)/taglib/include/taglib" TYPE FILE FILES
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2extendedheader.h"
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2frame.h"
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2header.h"
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2synchdata.h"
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2footer.h"
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2framefactory.h"
    "C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/id3v2tag.h"
    )
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("C:/College/Systems Capstone/temp/taglib-1.7/taglib/mpeg/id3v2/frames/cmake_install.cmake")

endif()

