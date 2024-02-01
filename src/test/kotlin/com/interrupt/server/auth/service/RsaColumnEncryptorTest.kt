package com.interrupt.server.auth.service

import com.interrupt.server.auth.config.RsaKeyProperties
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RsaColumnEncryptorTest {

    private val rsaKeyProperties: RsaKeyProperties = mockk<RsaKeyProperties>().also {


        every { it.publicKey } returns "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApDlV7fi1EPjKf4KOAWEgp9/980II+jNyIxCB0qyFs30LOMYsu8kKWRejJX5AFvBecP50lutZ7SbOCb4Ag6ktpLNrZBgDUQEwPnKu4Om8Ms/fKrJ6VYaaZwyq+fzbZNjk1BJ8gXMJsR0EPhwdm9Bmi8VjtDDQnrRITaysSzFPmbCPnzilb8NpzJXvusCjBG7rxvyH8BhvIjVkZ6udcONPe1KTfNpJzVTa806VGFRRkcJhEQi11myOEyOhhQU+sLc5J9CzClYV6vS9giqaY20dOfPfdEhRN2b71P/rf605ft7IZ7GcWuVie8SrMhWMGrt3aJUpXv/Pw9KyXPT9bvcqqwIDAQAB"
        every { it.privateKey } returns "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCkOVXt+LUQ+Mp/go4BYSCn3/3zQgj6M3IjEIHSrIWzfQs4xiy7yQpZF6MlfkAW8F5w/nSW61ntJs4JvgCDqS2ks2tkGANRATA+cq7g6bwyz98qsnpVhppnDKr5/Ntk2OTUEnyBcwmxHQQ+HB2b0GaLxWO0MNCetEhNrKxLMU+ZsI+fOKVvw2nMle+6wKMEbuvG/IfwGG8iNWRnq51w4097UpN82knNVNrzTpUYVFGRwmERCLXWbI4TI6GFBT6wtzkn0LMKVhXq9L2CKppjbR058990SFE3ZvvU/+t/rTl+3shnsZxa5WJ7xKsyFYwau3dolSle/8/D0rJc9P1u9yqrAgMBAAECggEARmjovTN1WlX46CuO++trR16jcGD/HVriVENXDf73AgoaPhjFhCvBeopGYiRbkRo0u6h1T2GPuDx7eTTT3p8x2Rwt7ofXICtsxlGARiEglf6G9bT4+2NPHuCRC8aMQBQ4I+7bH9s1LBE/SOvgC/aRiNq0EUUepPSxzU+7vS9irAcUmsCig49EGgrySmFSMVIxxb8wlW5dgIIUUKuGnQ0OxevCfL6cbL8lTIiar2LMwk8x+pDtX5WfBuJNqM/MBXbp0010UxqXvA/RTVuMMNeVsdZW1x9ZNkwc5DPTkOOBzHgw0k38c561+FZcMoKoe7gXV1lWlpDWOcJH3YrYNmRuxQKBgQDn/4aJL0HE2CGdQFh18qPAhyuuq0vMENZK+18WXbh1RVClU5EYktBDhA4WHBXErcLzp7JkY1I6kWpO6H1HT+4vm3zja3GXOnNsgGkv6juShJMhRgDRecYJaxbdvUNxUVpJB0TezVx2R+N/m2PxypfkSovwYDupxYuVzT/ownDEfQKBgQC1Ns9ZXGaymskPCqJxjLHj4VwgjPPuuHjX79VGEY2fzmwDSN4ch7aRhktG1LnyH9VZ3ve4oSBJx+kamzS1b6xGwdP7DlBdHopZ3ZLFz2y/gZ+n7GW3a4vmNPhUgxHVaCshrmDqtalztQ3bw/d4FI7j6+8e6WzBQRQCBxpBAm8cRwKBgCaOpUFvhTK96PhFlW4k4WpCDNR+y54ivjrABFGnSdzTpE+Vz+uRmuNdsymhiUyixF/SYyekCiqJuG1c3lVXx3JKGTuCw+IqaLyDqve3qibzubAx/jjSUEOIdMhtiCPxLVwtxRhycQeHcxvmK3on/Y6HhTJyC/hvSH+hGRoKFsvRAoGBAKr//KwzoIl7vKzf+Sgzfw4t8/SXbfxfwVDrdZL4L2+Up6J12XqMqkAqvNArHwiW94ol+BcWnNRiu9oqiZOIvMXMVA+9kyI8Fov0pXLaaKFSXWlljxqFdu+WHpwJTwPVl5Xh6nyxTiTsvhYal2SN6cS0N+IwnTrEAXBkaWbdOCIzAoGAAN1Jb6Ox3iRJ506zMRxFc3VyGfcFh0q4sOBTJBtHYx0nDkbUGcOmAr8Sm3dXavTSFaqDpZXfDfDo23t7ikVH7JhUEWKHASzc40XalrXuGO/yC5ir89wf/hm6Ee05lQExZq19loi9UNK9ITzKo8ukZh6FIT1B4gGh6FoM8d/+694="
    }

    private val rsaColumnEncryptor = RsaColumnEncryptor(rsaKeyProperties)

    @BeforeEach
    fun setUp() {
        rsaColumnEncryptor.init()
    }

    @Test
    fun `문자열을 RSA 암호화 한다`() {
        // given
        val original = "test"

        // when
        val encrypted = rsaColumnEncryptor.convertToDatabaseColumn(original)

        // then
        assertThat(encrypted).isNotEqualTo(original)
    }

    @Test
    fun `RSA 암호화를 할 때, null 값을 받으면 그대로 null 을 반환한다`() {
        // given
        val original = null

        // when
        val encrypted = rsaColumnEncryptor.convertToDatabaseColumn(original)

        // then
        assertThat(encrypted).isNull()
    }

    @Test
    fun `암호화 된 문자열을 RSA 복호화 한다`() {
        // given
        val encrypted = "jYtHdRB7gg2AVaTbAZOygg67oVwyg7ZzgzvYNhYRmRw6Yh1LMe+Y07M+43XN9wVbzA36Zc5DGCTn0KkRW9DPF2T0/Ls1wClwxeDJGUoBTXZibfi6I5k7yIskM2lsBWvzNu3rDvsZJ74IgPiccXx/Dm6zfyVdMtiRQdCc0aLWOdp+2bEh4nzCiaOlC5m6gnYGClr6mlk2Se+Z6AB9hifG/FmqMii0dI8NC8U/S0NT7gqBM+UbL1nfHM7B1mYz0wYfVC5XAanfx4fRNNWHc7DZ8qG4lVyQDgcCc0YaMAsCM4ywJzc8CW2SSMivd1fFLzwevDbMx4HNzZKZ6PWII1KJZA=="

        // when
        val decrypted = rsaColumnEncryptor.convertToEntityAttribute(encrypted)

        // then
        assertThat(decrypted).isEqualTo("test")
    }

    @Test
    fun `RSA 복호화를 할 때, null 을 받으면 그대로 null 을 반환한다`() {
        // given
        val encrypted = null

        // when
        val decrypted = rsaColumnEncryptor.convertToEntityAttribute(encrypted)

        // then
        assertThat(decrypted).isNull()
    }

}