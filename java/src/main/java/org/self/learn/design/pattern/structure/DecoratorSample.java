package org.self.learn.design.pattern.structure;

/**
 * 装饰器模式：
 * <p>
 * 对于target目标需要新增功能时，通过decorator将新增功能附加到target对象上.
 * decorator必须继承或实现target目标类
 * <p>
 * JDK中的应用：
 * <p>
 * InputStream 作为字节输入流，核心功能是字节流读取
 * BufferedInputStream GZIPInputStream 拥有一个共同的父类FilterInputStream作为顶层装饰器
 * BufferedInputStream GZIPInputStream是扩展了缓冲和解压功能的装饰器
 * <p>
 * 通过在JDK中的应用，实际意义如下：
 * <p>
 * 数据源需要扩展附加功能时: 可以通过在一个抽象的装饰器上面去扩展就要附加功能的装饰器即可
 * 同时数据源也可以扩展核心功能: 比如 FileInputStream, ByteInputStream... 均可以用装饰器装饰
 * <p>
 * 做到数据源的核心功能扩展与附加功能扩展分离，提升扩展性
 * <p>
 * 举一个例子: 在内文本容上附加<span><bold><u></u></bold></span>进行渲染
 *
 * @date 2020-04-19.
 */
public class DecoratorSample {

    public static void main(String[] args) {
        TextNode textNode = new TextNode("Text content!!!");

        //进行渲染
        TextNode spanTextDecorator = new SpanTextDecorator(textNode);
        System.out.println(spanTextDecorator.getContent());

        TextNode boldTextDecorator = new BoldTextDecorator(spanTextDecorator);
        System.out.println(boldTextDecorator.getContent());
    }


    private static class TextNode {
        public String content;

        public TextNode() {
        }

        public TextNode(String content) {
            this.content = content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    private abstract static class TextDecorator extends TextNode {
        protected TextNode target;

        public TextDecorator(TextNode target) {
            this.target = target;
            setTarget(target);
        }

        public abstract void setTarget(TextNode target);

        @Override
        public String getContent() {
            return target.content;
        }
    }

    private static class SpanTextDecorator extends TextDecorator {
        public SpanTextDecorator(TextNode target) {
            super(target);
        }

        @Override
        public void setTarget(TextNode textNode) {
            target.content = String.format("<span>%s</span>", textNode.getContent());
        }

    }

    private static class BoldTextDecorator extends TextDecorator {
        public BoldTextDecorator(TextNode target) {
            super(target);
        }

        @Override
        public void setTarget(TextNode textNode) {
            target.content = String.format("<bold>%s</bold>", textNode.getContent());
        }

    }
}
