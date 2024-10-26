package com.matou.smartcar.protocol;// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Obstacle.proto

public final class ObstacleOuterClass {
  private ObstacleOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ObstacleOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Obstacle)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 障碍物类型,1.机动车;2.非机动车(电动车,自行车等);3.行人;
     * </pre>
     *
     * <code>int32 type = 1;</code>
     */
    int getType();

    /**
     * <pre>
     * 障碍物位置,可选
     * </pre>
     *
     * <code>.Position position = 2;</code>
     */
    boolean hasPosition();
    /**
     * <pre>
     * 障碍物位置,可选
     * </pre>
     *
     * <code>.Position position = 2;</code>
     */
    PositionOuterClass.Position getPosition();
    /**
     * <pre>
     * 障碍物位置,可选
     * </pre>
     *
     * <code>.Position position = 2;</code>
     */
    PositionOuterClass.PositionOrBuilder getPositionOrBuilder();

    /**
     * <pre>
     * 障碍物危险级别,可选
     * </pre>
     *
     * <code>int32 priority = 3;</code>
     */
    int getPriority();

    /**
     * <pre>
     *障碍物速度,可选
     * </pre>
     *
     * <code>float speed = 4;</code>
     */
    float getSpeed();

    /**
     * <pre>
     * 航向角
     * </pre>
     *
     * <code>int32 heading = 5;</code>
     */
    int getHeading();
  }
  /**
   * <pre>
   *障碍物信息
   * </pre>
   *
   * Protobuf type {@code Obstacle}
   */
  public  static final class Obstacle extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:Obstacle)
      ObstacleOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Obstacle.newBuilder() to construct.
    private Obstacle(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Obstacle() {
      type_ = 0;
      priority_ = 0;
      speed_ = 0F;
      heading_ = 0;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Obstacle(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownFieldProto3(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {

              type_ = input.readInt32();
              break;
            }
            case 18: {
              PositionOuterClass.Position.Builder subBuilder = null;
              if (position_ != null) {
                subBuilder = position_.toBuilder();
              }
              position_ = input.readMessage(PositionOuterClass.Position.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(position_);
                position_ = subBuilder.buildPartial();
              }

              break;
            }
            case 24: {

              priority_ = input.readInt32();
              break;
            }
            case 37: {

              speed_ = input.readFloat();
              break;
            }
            case 40: {

              heading_ = input.readInt32();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ObstacleOuterClass.internal_static_Obstacle_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ObstacleOuterClass.internal_static_Obstacle_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ObstacleOuterClass.Obstacle.class, ObstacleOuterClass.Obstacle.Builder.class);
    }

    public static final int TYPE_FIELD_NUMBER = 1;
    private int type_;
    /**
     * <pre>
     * 障碍物类型,1.机动车;2.非机动车(电动车,自行车等);3.行人;
     * </pre>
     *
     * <code>int32 type = 1;</code>
     */
    public int getType() {
      return type_;
    }

    public static final int POSITION_FIELD_NUMBER = 2;
    private PositionOuterClass.Position position_;
    /**
     * <pre>
     * 障碍物位置,可选
     * </pre>
     *
     * <code>.Position position = 2;</code>
     */
    public boolean hasPosition() {
      return position_ != null;
    }
    /**
     * <pre>
     * 障碍物位置,可选
     * </pre>
     *
     * <code>.Position position = 2;</code>
     */
    public PositionOuterClass.Position getPosition() {
      return position_ == null ? PositionOuterClass.Position.getDefaultInstance() : position_;
    }
    /**
     * <pre>
     * 障碍物位置,可选
     * </pre>
     *
     * <code>.Position position = 2;</code>
     */
    public PositionOuterClass.PositionOrBuilder getPositionOrBuilder() {
      return getPosition();
    }

    public static final int PRIORITY_FIELD_NUMBER = 3;
    private int priority_;
    /**
     * <pre>
     * 障碍物危险级别,可选
     * </pre>
     *
     * <code>int32 priority = 3;</code>
     */
    public int getPriority() {
      return priority_;
    }

    public static final int SPEED_FIELD_NUMBER = 4;
    private float speed_;
    /**
     * <pre>
     *障碍物速度,可选
     * </pre>
     *
     * <code>float speed = 4;</code>
     */
    public float getSpeed() {
      return speed_;
    }

    public static final int HEADING_FIELD_NUMBER = 5;
    private int heading_;
    /**
     * <pre>
     * 航向角
     * </pre>
     *
     * <code>int32 heading = 5;</code>
     */
    public int getHeading() {
      return heading_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (type_ != 0) {
        output.writeInt32(1, type_);
      }
      if (position_ != null) {
        output.writeMessage(2, getPosition());
      }
      if (priority_ != 0) {
        output.writeInt32(3, priority_);
      }
      if (speed_ != 0F) {
        output.writeFloat(4, speed_);
      }
      if (heading_ != 0) {
        output.writeInt32(5, heading_);
      }
      unknownFields.writeTo(output);
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (type_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, type_);
      }
      if (position_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getPosition());
      }
      if (priority_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, priority_);
      }
      if (speed_ != 0F) {
        size += com.google.protobuf.CodedOutputStream
          .computeFloatSize(4, speed_);
      }
      if (heading_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(5, heading_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof ObstacleOuterClass.Obstacle)) {
        return super.equals(obj);
      }
      ObstacleOuterClass.Obstacle other = (ObstacleOuterClass.Obstacle) obj;

      boolean result = true;
      result = result && (getType()
          == other.getType());
      result = result && (hasPosition() == other.hasPosition());
      if (hasPosition()) {
        result = result && getPosition()
            .equals(other.getPosition());
      }
      result = result && (getPriority()
          == other.getPriority());
      result = result && (
          Float.floatToIntBits(getSpeed())
          == Float.floatToIntBits(
              other.getSpeed()));
      result = result && (getHeading()
          == other.getHeading());
      result = result && unknownFields.equals(other.unknownFields);
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getType();
      if (hasPosition()) {
        hash = (37 * hash) + POSITION_FIELD_NUMBER;
        hash = (53 * hash) + getPosition().hashCode();
      }
      hash = (37 * hash) + PRIORITY_FIELD_NUMBER;
      hash = (53 * hash) + getPriority();
      hash = (37 * hash) + SPEED_FIELD_NUMBER;
      hash = (53 * hash) + Float.floatToIntBits(
          getSpeed());
      hash = (37 * hash) + HEADING_FIELD_NUMBER;
      hash = (53 * hash) + getHeading();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static ObstacleOuterClass.Obstacle parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static ObstacleOuterClass.Obstacle parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static ObstacleOuterClass.Obstacle parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static ObstacleOuterClass.Obstacle parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(ObstacleOuterClass.Obstacle prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     *障碍物信息
     * </pre>
     *
     * Protobuf type {@code Obstacle}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:Obstacle)
        ObstacleOuterClass.ObstacleOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return ObstacleOuterClass.internal_static_Obstacle_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return ObstacleOuterClass.internal_static_Obstacle_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ObstacleOuterClass.Obstacle.class, ObstacleOuterClass.Obstacle.Builder.class);
      }

      // Construct using ObstacleOuterClass.Obstacle.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        type_ = 0;

        if (positionBuilder_ == null) {
          position_ = null;
        } else {
          position_ = null;
          positionBuilder_ = null;
        }
        priority_ = 0;

        speed_ = 0F;

        heading_ = 0;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return ObstacleOuterClass.internal_static_Obstacle_descriptor;
      }

      public ObstacleOuterClass.Obstacle getDefaultInstanceForType() {
        return ObstacleOuterClass.Obstacle.getDefaultInstance();
      }

      public ObstacleOuterClass.Obstacle build() {
        ObstacleOuterClass.Obstacle result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public ObstacleOuterClass.Obstacle buildPartial() {
        ObstacleOuterClass.Obstacle result = new ObstacleOuterClass.Obstacle(this);
        result.type_ = type_;
        if (positionBuilder_ == null) {
          result.position_ = position_;
        } else {
          result.position_ = positionBuilder_.build();
        }
        result.priority_ = priority_;
        result.speed_ = speed_;
        result.heading_ = heading_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ObstacleOuterClass.Obstacle) {
          return mergeFrom((ObstacleOuterClass.Obstacle)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ObstacleOuterClass.Obstacle other) {
        if (other == ObstacleOuterClass.Obstacle.getDefaultInstance()) return this;
        if (other.getType() != 0) {
          setType(other.getType());
        }
        if (other.hasPosition()) {
          mergePosition(other.getPosition());
        }
        if (other.getPriority() != 0) {
          setPriority(other.getPriority());
        }
        if (other.getSpeed() != 0F) {
          setSpeed(other.getSpeed());
        }
        if (other.getHeading() != 0) {
          setHeading(other.getHeading());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ObstacleOuterClass.Obstacle parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ObstacleOuterClass.Obstacle) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private int type_ ;
      /**
       * <pre>
       * 障碍物类型,1.机动车;2.非机动车(电动车,自行车等);3.行人;
       * </pre>
       *
       * <code>int32 type = 1;</code>
       */
      public int getType() {
        return type_;
      }
      /**
       * <pre>
       * 障碍物类型,1.机动车;2.非机动车(电动车,自行车等);3.行人;
       * </pre>
       *
       * <code>int32 type = 1;</code>
       */
      public Builder setType(int value) {

        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 障碍物类型,1.机动车;2.非机动车(电动车,自行车等);3.行人;
       * </pre>
       *
       * <code>int32 type = 1;</code>
       */
      public Builder clearType() {

        type_ = 0;
        onChanged();
        return this;
      }

      private PositionOuterClass.Position position_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          PositionOuterClass.Position, PositionOuterClass.Position.Builder, PositionOuterClass.PositionOrBuilder> positionBuilder_;
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public boolean hasPosition() {
        return positionBuilder_ != null || position_ != null;
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public PositionOuterClass.Position getPosition() {
        if (positionBuilder_ == null) {
          return position_ == null ? PositionOuterClass.Position.getDefaultInstance() : position_;
        } else {
          return positionBuilder_.getMessage();
        }
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public Builder setPosition(PositionOuterClass.Position value) {
        if (positionBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          position_ = value;
          onChanged();
        } else {
          positionBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public Builder setPosition(
          PositionOuterClass.Position.Builder builderForValue) {
        if (positionBuilder_ == null) {
          position_ = builderForValue.build();
          onChanged();
        } else {
          positionBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public Builder mergePosition(PositionOuterClass.Position value) {
        if (positionBuilder_ == null) {
          if (position_ != null) {
            position_ =
              PositionOuterClass.Position.newBuilder(position_).mergeFrom(value).buildPartial();
          } else {
            position_ = value;
          }
          onChanged();
        } else {
          positionBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public Builder clearPosition() {
        if (positionBuilder_ == null) {
          position_ = null;
          onChanged();
        } else {
          position_ = null;
          positionBuilder_ = null;
        }

        return this;
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public PositionOuterClass.Position.Builder getPositionBuilder() {

        onChanged();
        return getPositionFieldBuilder().getBuilder();
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      public PositionOuterClass.PositionOrBuilder getPositionOrBuilder() {
        if (positionBuilder_ != null) {
          return positionBuilder_.getMessageOrBuilder();
        } else {
          return position_ == null ?
              PositionOuterClass.Position.getDefaultInstance() : position_;
        }
      }
      /**
       * <pre>
       * 障碍物位置,可选
       * </pre>
       *
       * <code>.Position position = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          PositionOuterClass.Position, PositionOuterClass.Position.Builder, PositionOuterClass.PositionOrBuilder>
          getPositionFieldBuilder() {
        if (positionBuilder_ == null) {
          positionBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              PositionOuterClass.Position, PositionOuterClass.Position.Builder, PositionOuterClass.PositionOrBuilder>(
                  getPosition(),
                  getParentForChildren(),
                  isClean());
          position_ = null;
        }
        return positionBuilder_;
      }

      private int priority_ ;
      /**
       * <pre>
       * 障碍物危险级别,可选
       * </pre>
       *
       * <code>int32 priority = 3;</code>
       */
      public int getPriority() {
        return priority_;
      }
      /**
       * <pre>
       * 障碍物危险级别,可选
       * </pre>
       *
       * <code>int32 priority = 3;</code>
       */
      public Builder setPriority(int value) {

        priority_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 障碍物危险级别,可选
       * </pre>
       *
       * <code>int32 priority = 3;</code>
       */
      public Builder clearPriority() {

        priority_ = 0;
        onChanged();
        return this;
      }

      private float speed_ ;
      /**
       * <pre>
       *障碍物速度,可选
       * </pre>
       *
       * <code>float speed = 4;</code>
       */
      public float getSpeed() {
        return speed_;
      }
      /**
       * <pre>
       *障碍物速度,可选
       * </pre>
       *
       * <code>float speed = 4;</code>
       */
      public Builder setSpeed(float value) {

        speed_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *障碍物速度,可选
       * </pre>
       *
       * <code>float speed = 4;</code>
       */
      public Builder clearSpeed() {

        speed_ = 0F;
        onChanged();
        return this;
      }

      private int heading_ ;
      /**
       * <pre>
       * 航向角
       * </pre>
       *
       * <code>int32 heading = 5;</code>
       */
      public int getHeading() {
        return heading_;
      }
      /**
       * <pre>
       * 航向角
       * </pre>
       *
       * <code>int32 heading = 5;</code>
       */
      public Builder setHeading(int value) {

        heading_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 航向角
       * </pre>
       *
       * <code>int32 heading = 5;</code>
       */
      public Builder clearHeading() {

        heading_ = 0;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFieldsProto3(unknownFields);
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:Obstacle)
    }

    // @@protoc_insertion_point(class_scope:Obstacle)
    private static final ObstacleOuterClass.Obstacle DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new ObstacleOuterClass.Obstacle();
    }

    public static ObstacleOuterClass.Obstacle getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Obstacle>
        PARSER = new com.google.protobuf.AbstractParser<Obstacle>() {
      public Obstacle parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new Obstacle(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Obstacle> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<Obstacle> getParserForType() {
      return PARSER;
    }

    public ObstacleOuterClass.Obstacle getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_Obstacle_descriptor;
  private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_Obstacle_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\016Obstacle.proto\032\016Position.proto\"g\n\010Obst" +
      "acle\022\014\n\004type\030\001 \001(\005\022\033\n\010position\030\002 \001(\0132\t.P" +
      "osition\022\020\n\010priority\030\003 \001(\005\022\r\n\005speed\030\004 \001(\002" +
      "\022\017\n\007heading\030\005 \001(\005b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          PositionOuterClass.getDescriptor(),
        }, assigner);
    internal_static_Obstacle_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_Obstacle_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_Obstacle_descriptor,
        new String[] { "Type", "Position", "Priority", "Speed", "Heading", });
    PositionOuterClass.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
