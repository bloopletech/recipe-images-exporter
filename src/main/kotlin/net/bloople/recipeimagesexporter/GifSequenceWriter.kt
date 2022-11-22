package net.bloople.recipeimagesexporter

import java.awt.image.RenderedImage
import java.io.Closeable
import javax.imageio.*
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.ImageOutputStream

class GifSequenceWriter(out: ImageOutputStream, imageType: Int, delay: Int, loop: Boolean) : Closeable {
    private var writer: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()
    private var params: ImageWriteParam = writer.defaultWriteParam
    private var metadata: IIOMetadata

    init {
        val imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType)
        metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, params)

        val metaFormatName = metadata.nativeMetadataFormatName
        val root = metadata.getAsTree(metaFormatName) as IIOMetadataNode

        getNode(root, "GraphicControlExtension").apply {
            setAttribute("disposalMethod", "none")
            setAttribute("userInputFlag", "FALSE")
            setAttribute("transparentColorFlag", "FALSE")
            setAttribute("delayTime", (delay / 10).toString())
            setAttribute("transparentColorIndex", "0")
        }

        getNode(root, "ApplicationExtensions").appendChild(IIOMetadataNode("ApplicationExtension").apply {
            setAttribute("applicationID", "NETSCAPE")
            setAttribute("authenticationCode", "2.0")
            val loopContinuously = if(loop) 0 else 1
            userObject = byteArrayOf(0x1, (loopContinuously and 0xFF).toByte(), 0x0)
        })

        metadata.setFromTree(metaFormatName, root)

        writer.output = out
        writer.prepareWriteSequence(null)
    }

    fun writeToSequence(img: RenderedImage) {
        writer.writeToSequence(IIOImage(img, null, metadata), params)
    }

    override fun close() {
        writer.endWriteSequence()
    }

    companion object {
        private fun getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode {
            val nNodes = rootNode.length
            for(i in 0 until nNodes) {
                if(rootNode.item(i).nodeName.equals(nodeName, ignoreCase = true)) {
                    return rootNode.item(i) as IIOMetadataNode
                }
            }
            val node = IIOMetadataNode(nodeName)
            rootNode.appendChild(node)
            return node
        }
    }
}